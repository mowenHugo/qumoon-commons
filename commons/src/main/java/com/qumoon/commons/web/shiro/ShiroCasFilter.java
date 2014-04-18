package com.qumoon.commons.web.shiro;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kevin
 */
public class ShiroCasFilter extends AuthenticationFilter {

  private String casPath;

  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String realServletPath = httpRequest.getServletPath();
    if (null != realServletPath && realServletPath.equals(casPath)) {
      return true;
    } else {
      WebUtils2.saveTargetUrl(request);
      redirectToLogin(request, response);
      return false;
    }
  }

  public String getCasPath() {
    return casPath;
  }

  public void setCasPath(String casPath) {
    this.casPath = casPath;
  }
}
