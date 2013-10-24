package com.trilemon.commons.web.filter;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * 支持 PUT/DELETE，使用 X-HTTP-Method-Override 头
 */
public class MethodOverrideFilter extends OncePerRequestFilter {
    public static final String DEFAULT_OVERRIDE_HEADER = "X-HTTP-Method-Override";
    public static final String DEFAULT_METHOD_PARAM = "_method";

    private String methodParam = DEFAULT_METHOD_PARAM;
    private String overrideHeader = DEFAULT_OVERRIDE_HEADER;


    public void setOverrideHeader(String header) {
        Assert.hasText(header, "'override header' must not be empty");
        this.overrideHeader = header;
    }

    public void setMethodParam(String methodParam) {
        Assert.hasText(methodParam, "'methodParam' must not be empty");
        this.methodParam = methodParam;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String paramValue = request.getParameter(this.methodParam);
        String headerValue = request.getHeader(this.overrideHeader);

        if ("POST".equals(request.getMethod()) && (StringUtils.hasLength(paramValue) || StringUtils.hasLength(headerValue))) {
            String method = StringUtils.hasLength(paramValue) ? paramValue : headerValue;
            HttpServletRequest wrapper = new HttpMethodRequestWrapper(request, method.toUpperCase(Locale.ENGLISH));
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Simple {@link javax.servlet.http.HttpServletRequest} wrapper that returns the supplied method for
     * {@link javax.servlet.http.HttpServletRequest#getMethod()}.
     */
    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final String method;

        public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
            super(request);
            this.method = method;
        }

        @Override
        public String getMethod() {
            return this.method;
        }
    }
}
