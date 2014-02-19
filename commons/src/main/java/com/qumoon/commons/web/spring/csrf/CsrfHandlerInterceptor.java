package com.qumoon.commons.web.spring.csrf;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;

/**
 * A Spring MVC <code>HandlerInterceptor</code> which is responsible to enforce CSRF token validity on incoming posts
 * requests. The interceptor should be registered with Spring MVC servlet using the following syntax:
 * <pre>
 *   &lt;mvc:interceptors&gt;
 *        &lt;bean class="com.eyallupu.blog.springmvc.controller.csrf.CsrfHandlerInterceptor"/&gt;
 *   &lt;/mvc:interceptors&gt;
 *   </pre>
 *
 * @author Eyal Lupu
 * @see CsrfAndXssRequestDataValueProcessor
 */
public class CsrfHandlerInterceptor extends HandlerInterceptorAdapter {
    private CsrfTokenManager csrfTokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            // Not a POST - allow the request
            return true;
        } else {
            // This is a POST request - need to check the CSRF token
            request.getSession();//touch and generate session
            LinkedList<String> sessionTokens = csrfTokenManager.getTokensInSession(request.getRequestedSessionId());
            String requestToken = csrfTokenManager.getTokenInRequest(request);
            try {
                if (sessionTokens.contains(requestToken)) {
                    return true;
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRF value");
                    return false;
                }
            } finally {
                csrfTokenManager.destroyTokenInSession(request.getRequestedSessionId(), requestToken);
            }
        }
    }

    public void setCsrfTokenManager(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }
}
