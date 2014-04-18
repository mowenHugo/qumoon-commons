package com.qumoon.commons.web.filter;

import com.google.common.base.Preconditions;

import com.qumoon.commons.web.CookieSupport;
import com.qumoon.commons.web.exception.CookieRejectedException;
import com.qumoon.commons.web.exception.RedirectLocationRejectedException;
import com.qumoon.commons.web.exception.ResponseHeaderRejectedException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author kevin
 */
public class SecurityResponseWrapper extends HttpServletResponseWrapper {

  private static final String LOCATION_HEADER = "Location";
  private static final String SET_COOKIE_HEADER = "Set-Cookie";
  private final SecurityResponseHeaderChecker securityResponseHeaderChecker = new SecurityResponseHeaderChecker();
  private HttpServletRequest request;

  public SecurityResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
    super(response);
    this.request = request;
  }

  static String normalizeLocation(String location, HttpServletRequest request) {
    location = Preconditions.checkNotNull(StringUtils.trimToNull(location), "no redirect location");

    URI locationURI = URI.create(location);

    if (!locationURI.isAbsolute()) {
      URI baseUri = URI.create(request.getRequestURL().toString());
      locationURI = baseUri.resolve(locationURI);
    }

    return locationURI.normalize().toString();
  }

  @Override
  public void addDateHeader(String name, long date) {
    super.addDateHeader(checkHeaderName(name), date);
  }

  @Override
  public void setDateHeader(String name, long date) {
    super.setDateHeader(checkHeaderName(name), date);
  }

  @Override
  public void addIntHeader(String name, int value) {
    super.addIntHeader(checkHeaderName(name), value);
  }

  @Override
  public void setIntHeader(String name, int value) {
    super.setIntHeader(checkHeaderName(name), value);
  }

  @Override
  public void addHeader(String name, String value) {
    name = StringUtils.trimToNull(name);

    if (LOCATION_HEADER.equalsIgnoreCase(name)) {
      value = checkRedirectLocation(value, false);

      if (value != null) {
        super.setHeader(LOCATION_HEADER, value); // force SET header
      }
    } else if (SET_COOKIE_HEADER.equalsIgnoreCase(name)) {
      value = checkCookieHeaderValue(name, value, false);

      if (value != null) {
        super.addHeader(SET_COOKIE_HEADER, value);
      }
    } else {
      name = checkHeaderName(name);
      value = checkHeaderValue(name, value);

      if (value != null) {
        super.addHeader(name, value);
      }
    }
  }

  @Override
  public void setHeader(String name, String value) {
    name = StringUtils.trimToNull(name);

    if (LOCATION_HEADER.equalsIgnoreCase(name)) {
      value = checkRedirectLocation(value, false);

      if (value != null) {
        super.setHeader(LOCATION_HEADER, value);
      }
    } else if (SET_COOKIE_HEADER.equalsIgnoreCase(name)) {
      value = checkCookieHeaderValue(name, value, true);

      if (value != null) {
        super.setHeader(SET_COOKIE_HEADER, value);
      }
    } else {
      name = checkHeaderName(name);
      value = checkHeaderValue(name, value);

      if (value != null) {
        super.setHeader(name, value);
      }
    }
  }

  private String checkHeaderName(String name) throws ResponseHeaderRejectedException {
    Preconditions.checkNotNull(name, "header name is null"); // name==null报错

    String newName = securityResponseHeaderChecker.checkHeaderName(name);

    if (newName == null) {
      throw new ResponseHeaderRejectedException("HTTP header rejected: " +
                                                StringEscapeUtils.escapeJava(name));
    }

    return newName;
  }

  private String checkHeaderValue(String name, String value) throws ResponseHeaderRejectedException {
    if (value == null) {
      return null; // value==null返回
    }

    String newValue = securityResponseHeaderChecker.checkHeaderValue(name, value);

    if (newValue == null) {
      throw new ResponseHeaderRejectedException("HTTP header rejected: " + StringEscapeUtils.escapeJava(name)
                                                + "=" + StringEscapeUtils.escapeJava(value));
    }

    return newValue;
  }

  @Override
  public void addCookie(Cookie cookie) {
    Cookie newCookie = checkCookie(cookie);
    CookieSupport newCookieSupport;

    if (newCookie instanceof CookieSupport) {
      newCookieSupport = (CookieSupport) newCookie;
    } else {
      newCookieSupport = new CookieSupport(newCookie); // 将cookie强制转化成cookie support
    }

    newCookieSupport.addCookie(this); // 通过set-cookie header来添加cookie，以便统一监管
  }

  private Cookie checkCookie(Cookie cookie) throws CookieRejectedException {
    Preconditions.checkNotNull(cookie, "no cookie");

    Cookie newCookie = securityResponseHeaderChecker.checkCookie(cookie);

    if (newCookie == null) {
      throw new CookieRejectedException("Cookie rejected: " + StringEscapeUtils.escapeJava(cookie.getName()) + "="
                                        + StringEscapeUtils.escapeJava(cookie.getValue()));
    }

    return newCookie;
  }

  private String checkCookieHeaderValue(String name, String value, boolean setHeader) throws CookieRejectedException {
    if (value == null) {
      return null; // value==null返回
    }

    String newValue = securityResponseHeaderChecker.checkCookieHeaderValue(name, value,
                                                                           setHeader);

    if (newValue == null) {
      throw new CookieRejectedException("Set-Cookie rejected: " + StringEscapeUtils.escapeJava(value));
    }

    return newValue;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    msg = checkStatusMessage(sc, msg);

    if (msg == null) {
      super.sendError(sc);
    } else {
      super.sendError(sc, msg);
    }
  }

  @Override
  @Deprecated
  public void setStatus(int sc, String msg) {
    msg = checkStatusMessage(sc, msg);

    if (msg == null) {
      super.setStatus(sc);
    } else {
      super.setStatus(sc, msg);
    }
  }

  private String checkStatusMessage(int sc, String msg) {
    if (msg != null) {
      msg = securityResponseHeaderChecker.checkStatusMessage(sc, msg);
    }

    return msg;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    super.sendRedirect(checkRedirectLocation(location, true));
  }

  private String checkRedirectLocation(String location, boolean notNull) throws RedirectLocationRejectedException {
    String newLocation = StringUtils.trimToNull(location);

    if (newLocation == null && !notNull) {
      return null;
    }

    newLocation = normalizeLocation(newLocation, request);

    newLocation = securityResponseHeaderChecker.checkRedirectLocation(newLocation);

    if (newLocation == null) {
      throw new RedirectLocationRejectedException("Redirect location rejected: "
                                                  + StringEscapeUtils.escapeJava(location));
    }

    return newLocation;
  }
}