package com.qumoon.commons.web.shiro;

import com.qumoon.commons.WebUtils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kevin
 */
public class WebUtils2 {

  public static final String SAVED_TARGET_URL_KEY = "shiroSavedTargetUrl";

  public static void saveTargetUrl(ServletRequest request) {
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession();
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String targetUrl = WebUtils.getRequestHost(httpRequest);
    session.setAttribute(SAVED_TARGET_URL_KEY, targetUrl + httpRequest.getRequestURI());
  }

  public static String getSavedTargetUrl() {
    String targetUrl = null;
    Subject subject = SecurityUtils.getSubject();
    Session session = subject.getSession(false);
    if (session != null) {
      targetUrl = (String) session.getAttribute(SAVED_TARGET_URL_KEY);
    }
    return targetUrl;
  }
}
