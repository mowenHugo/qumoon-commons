package com.qumoon.commons.web.filter;

import com.qumoon.commons.HumanReadableSize;
import com.qumoon.commons.web.CookieSupport;
import com.qumoon.commons.web.exception.ResponseHeaderRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qumoon.commons.CommonConstant.EMPTY_STRING;

/**
 * @author kevin
 */
public class SecurityResponseHeaderChecker {
    public static final HumanReadableSize MAX_SET_COOKIE_SIZE_DEFAULT = new HumanReadableSize("7k");
    private static final String COOKIE_LENGTH_ATTR = "_COOKIE_LENGTH_";
    private static final Pattern crlf = Pattern.compile("\\r\\n|\\r|\\n");
    private static Logger logger = LoggerFactory.getLogger(SecurityResponseHeaderChecker.class);
    private CookieLengthAccumulator cookieLengthAccumulator = null;
    private HumanReadableSize maxSetCookieSize;

    public SecurityResponseHeaderChecker() {
        this(null);
    }

    public SecurityResponseHeaderChecker(HttpServletRequest request) {
        if (request == null) {
            cookieLengthAccumulator = new ThreadLocalBasedCookieLengthAccumulator();
        } else {
            cookieLengthAccumulator = new RequestBasedCookieLengthAccumulator(request);
        }
    }

    public HumanReadableSize getMaxSetCookieSize() {
        return maxSetCookieSize == null || maxSetCookieSize.getValue() <= 0 ? MAX_SET_COOKIE_SIZE_DEFAULT
                : maxSetCookieSize;
    }

    public String checkHeaderName(String name) {
        if (containsCRLF(name)) {
            String msg = "Invalid response header: " + StringEscapeUtils.escapeJava(name);
            logger.error(msg);
            throw new ResponseHeaderRejectedException(msg);
        }
        return name;
    }

    public String checkHeaderValue(String name, String value) {
        return ObjectUtils.defaultIfNull(filterCRLF(value, "header " + name), value);
    }

    public Cookie checkCookie(Cookie cookie) {
        String name = cookie.getName();

        if (containsCRLF(name)) {
            logger.error("Invalid cookie name: " + StringEscapeUtils.escapeJava(name));
            return null;
        }

        String value = cookie.getValue();
        String filteredValue = filterCRLF(value, "cookie " + name);

        if (filteredValue == null) {
            return cookie;
        } else {
            CookieSupport newCookie = new CookieSupport(cookie);
            newCookie.setValue(filteredValue);
            return newCookie;
        }
    }

    public String checkCookieHeaderValue(String name, String value, boolean setHeader) {
        if (value != null) {
            int maxSetCookieSize = (int) getMaxSetCookieSize().getValue();
            int length = cookieLengthAccumulator.getLength();

            if (length + value.length() > maxSetCookieSize) {
                logger.error(
                        "Cookie size exceeds the max value: {} + {} > maxSize {}.  Cookie is ignored: {}",
                        new Object[]{length, value.length(), getMaxSetCookieSize(), value});

                return EMPTY_STRING;
            } else {
                if (setHeader) {
                    cookieLengthAccumulator.setCookie(value);
                } else {
                    cookieLengthAccumulator.addCookie(value);
                }
            }
        }

        return value;
    }

    public String checkStatusMessage(int sc, String msg) {
        return StringEscapeUtils.escapeHtml4(msg);
    }

    public String checkRedirectLocation(String location) {
        return ObjectUtils.defaultIfNull(filterCRLF(location, "redirectLocation"), location);
    }

    private boolean containsCRLF(String str) {
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                switch (str.charAt(i)) {
                    case '\r':
                    case '\n':
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * 如果不包含CRLF，则返回<code>null</code>，否则除去所有CRLF，替换成空格。
     */
    private String filterCRLF(String value, String logInfo) {
        if (containsCRLF(value)) {
            logger.warn("Found CRLF in {}: {}", logInfo, StringEscapeUtils.escapeJava(value));

            StringBuffer sb = new StringBuffer();
            Matcher m = crlf.matcher(value);

            while (m.find()) {
                m.appendReplacement(sb, " ");
            }

            m.appendTail(sb);

            return sb.toString();
        }

        return null;
    }

    private static abstract class CookieLengthAccumulator {
        public final void addCookie(String cookie) {
            setLength(getLength() + cookie.length());
        }

        public final void setCookie(String cookie) {
            setLength(cookie.length());
        }

        public abstract int getLength();

        protected abstract void setLength(int length);

        protected abstract void reset();
    }

    private final class RequestBasedCookieLengthAccumulator extends CookieLengthAccumulator {
        private final ServletRequest request;

        private RequestBasedCookieLengthAccumulator(ServletRequest request) {
            this.request = request;
        }

        @Override
        public int getLength() {
            Object value = request.getAttribute(COOKIE_LENGTH_ATTR);

            if (value instanceof Integer) {
                return (Integer) value;
            } else {
                return 0;
            }
        }

        @Override
        protected void setLength(int length) {
            request.setAttribute(COOKIE_LENGTH_ATTR, length);
        }

        @Override
        protected void reset() {
            request.removeAttribute(COOKIE_LENGTH_ATTR);
        }
    }

    private final class ThreadLocalBasedCookieLengthAccumulator extends CookieLengthAccumulator {
        private final ThreadLocal<Integer> cookieLengthHolder = new ThreadLocal<Integer>();

        @Override
        public int getLength() {
            Object value = cookieLengthHolder.get();

            if (value instanceof Integer) {
                return (Integer) value;
            } else {
                return 0;
            }
        }

        @Override
        protected void setLength(int length) {
            cookieLengthHolder.set(length);
        }

        @Override
        protected void reset() {
            cookieLengthHolder.remove();
        }
    }
}
