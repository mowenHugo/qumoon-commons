package com.qumoon.commons.web.spring.csrf;

import com.google.common.collect.Maps;

import com.qumoon.commons.web.HTMLInputChecker;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import static com.qumoon.commons.CommonConstant.EMPTY_STRING_ARRAY;

/**
 * A <code>RequestDataValueProcessor</code> that pushes a hidden field with a CSRF token into forms. This process
 * implements the {@link #getExtraHiddenFields(HttpServletRequest)} method to push the CSRF token obtained from {@link
 * CsrfTokenManager}. To register this processor to automatically process all Spring based forms register it as a Spring
 * bean named 'requestDataValueProcessor' as shown below:
 * <pre>
 *  &lt;bean name="requestDataValueProcessor" class="com.eyallupu.blog.springmvc.controller.csrf.CsrfAndXssRequestDataValueProcessor"/&gt;
 * </pre>
 *
 * @author Eyal Lupu
 */
public class CsrfAndXssRequestDataValueProcessor implements RequestDataValueProcessor {

  private HTMLInputChecker filter;
  private Map<String, Set<String>> allowed;
  private String[] deniedTags;
  private String[] selfClosingTags;
  private String[] needClosingTags;
  private String[] allowedProtocols;
  private String[] protocolAttrs;
  private String[] removeBlanks;
  private String[] allowedEntities;
  private CsrfTokenManager csrfTokenManager;

  public CsrfAndXssRequestDataValueProcessor() {
    allowed = Maps.newHashMap();
    deniedTags = ObjectUtils.defaultIfNull(deniedTags, EMPTY_STRING_ARRAY);
    selfClosingTags = ObjectUtils.defaultIfNull(selfClosingTags, EMPTY_STRING_ARRAY);
    needClosingTags = ObjectUtils.defaultIfNull(needClosingTags, EMPTY_STRING_ARRAY);
    allowedProtocols = ObjectUtils.defaultIfNull(allowedProtocols, EMPTY_STRING_ARRAY);
    protocolAttrs = ObjectUtils.defaultIfNull(protocolAttrs, EMPTY_STRING_ARRAY);
    removeBlanks = ObjectUtils.defaultIfNull(removeBlanks, EMPTY_STRING_ARRAY);
    allowedEntities = ObjectUtils.defaultIfNull(allowedEntities, EMPTY_STRING_ARRAY);

    filter = new HTMLInputChecker(allowed, deniedTags, selfClosingTags, needClosingTags, allowedProtocols,
                                  protocolAttrs, removeBlanks, allowedEntities);
  }

  @Override
  public String processAction(HttpServletRequest request, String action) {
    return action;
  }

  @Override
  public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
    return filter.filter(value);
  }

  @Override
  public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
    if (request.getMethod().equalsIgnoreCase("POST")) {
      Map<String, String> hiddenFields = Maps.newHashMap();
      hiddenFields.put(CsrfTokenManager.CSRF_PARAM_NAME, csrfTokenManager.getTokenInSession(request.getSession
          ().getId()));
      return hiddenFields;
    }
    return null;
  }

  @Override
  public String processUrl(HttpServletRequest request, String url) {
    return url;
  }

  public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
    this.csrfTokenManager = csrfTokenManager;
  }
}
