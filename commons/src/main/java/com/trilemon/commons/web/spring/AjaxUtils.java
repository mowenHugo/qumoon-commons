package com.trilemon.commons.web.spring;

import org.springframework.web.context.request.WebRequest;

/**
 * @author kevin
 */
public class AjaxUtils {
    private AjaxUtils() {
    }

    public static boolean isAjaxRequest(WebRequest webRequest) {
        String requestedWith = webRequest.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    public static boolean isAjaxUploadRequest(WebRequest webRequest) {
        return webRequest.getParameter("ajaxUpload") != null;
    }
}
