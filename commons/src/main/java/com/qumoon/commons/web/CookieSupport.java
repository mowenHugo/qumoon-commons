package com.qumoon.commons.web;

import com.qumoon.commons.web.tomcat.ServerCookie;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 扩展原cookie，使之支持HttpOnly cookie。
 *
 * @author Michael Zhou
 */
public class CookieSupport extends Cookie {
    private final static Logger log               = LoggerFactory.getLogger(CookieSupport.class);
    private final static FastMethod getHttpOnlyMethod = getHttpOnlyMethod();
    private boolean httpOnly;

    /** 创建一个cookie。 */
    public CookieSupport(String name, String value) {
        super(checkNotNull( StringUtils.trimToNull(name), "cookieName"), value);
    }

    /** 复制一个cookie。 */
    public CookieSupport(Cookie cookie) {
        this(cookie, null);
    }

    /** 复制一个cookie，修改cookie的名称。 */
    public CookieSupport(Cookie cookie, String name) {
        super(checkNotNull(getCookieName(cookie, name), "cookieName"), cookie.getValue());

        setVersion(cookie.getVersion());
        setMaxAge(cookie.getMaxAge());
        setSecure(cookie.getSecure());

        String comment = cookie.getComment();

        if (!StringUtils.isEmpty(comment)) {
            setComment(comment);
        }

        String domain = cookie.getDomain();

        if (!StringUtils.isEmpty(domain)) {
            setDomain(domain);
        }

        String path = cookie.getPath();

        if (!StringUtils.isEmpty(path)) {
            setPath(path);
        }

        if (cookie instanceof CookieSupport) {
            setHttpOnly(((CookieSupport) cookie).getHttpOnly());
        } else if (getHttpOnlyMethod != null) {
            try {
                setHttpOnly((Boolean) getHttpOnlyMethod.invoke(cookie, new Object[0]));
            } catch (InvocationTargetException e) {
                log.warn("Invocation of Cookie.isHttpOnly() failed", e.getTargetException());
            }
        }
    }

    /** 对于servlet spec 3.0，已经支持<code>isHttpOnly</code>方法。 */
    private static FastMethod getHttpOnlyMethod() {
        Method m = null;

        try {
            m = Cookie.class.getMethod("isHttpOnly"); // servlet 3.0 spec draft
        } catch (Exception e) {
            try {
                m = Cookie.class.getMethod("getHttpOnly"); // 另一种可能
            } catch (Exception ee) {
            }
        }

        if (m != null) {
            log.debug("Method Cookie.isHttpOnly() defined in current version of servlet api.  CookieSupport will make use of it.");
            return FastClass.create(CookieSupport.class.getClassLoader(), Cookie.class).getMethod(m);
        }

        log.debug("No method Cookie.isHttpOnly() defined in current version of servlet api.");

        return null;
    }

    private static String getCookieName(Cookie cookie, String name) {
        name =  StringUtils.trimToNull(name);

        if (name == null) {
            name =  StringUtils.trimToNull(cookie.getName());
        }

        return name;
    }

    /** 是否生成IE6支持的HttpOnly标记。 */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /** 是否生成IE6支持的HttpOnly标记。 */
    public boolean getHttpOnly() {
        return httpOnly;
    }

    /** 是否生成IE6支持的HttpOnly标记。 */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public void setDomain(String domain) {
        domain = StringUtils.trimToEmpty(domain);

        if (!StringUtils.isEmpty(domain) && !domain.startsWith(".")) {
            domain = "." + domain;
        }

        super.setDomain(domain); // 根据RFC2109，确保以“.”为前缀
    }

    /** 将cookie添加到response中。 */
    public void addCookie(HttpServletResponse response) {
        response.addHeader(getCookieHeaderName(), getCookieHeaderValue());
    }

    /** 取得cookie header的名称。 */
    public String getCookieHeaderName() {
        return ServerCookie.getCookieHeaderName(getVersion());
    }

    /**
     * 取得cookie header的值。
     *
     * @throws IllegalArgumentException 假如cookie value中包含非法值
     */
    public String getCookieHeaderValue() throws IllegalArgumentException {
        return appendCookieHeaderValue(new StringBuilder()).toString();
    }

    private StringBuilder appendCookieHeaderValue(StringBuilder buf) throws IllegalArgumentException {
        int version = getVersion();
        String name = ObjectUtils.defaultIfNull(getName(), "");
        String value = getValue();
        String path = getPath();
        String domain = StringUtils.trimToNull(getDomain());
        String comment =  StringUtils.trimToNull(getComment());
        int maxAge = StringUtils.isEmpty(getValue()) ? 0 : getMaxAge(); // empty value means remove cookie
        boolean secure = getSecure();
        boolean httpOnly = getHttpOnly();

        ServerCookie.appendCookieValue(buf, version, name, value, path, domain, comment, maxAge, secure, httpOnly);

        return buf;
    }

    /**
     * 生成set-cookie header的值，即使cookie value中包含非法值，也不会报错。
     * <p>
     * 请不要使用<code>toString()</code>方法来生成cookie header，而应该使用
     * <code>getCookieHeaderValue()</code>来取代。
     * </p>
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder().append(getCookieHeaderName()).append(": ");
        int length = buf.length();

        try {
            appendCookieHeaderValue(buf);
        } catch (IllegalArgumentException e) {
            buf.setLength(length);
            buf.append(e.getMessage());
        }

        return buf.toString();
    }
}
