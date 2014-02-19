package com.qumoon.commons.web.spring.csrf;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.qumoon.commons.web.session.HttpSessionStore;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

final class CsrfTokenManager {
    /**
     * The token parameter name
     */
    public static final String CSRF_PARAM_NAME = "CSRFToken";
    private static final String CSRF_TOKEN_SEPARATOR = "/";
    /**
     * The location on the session which stores the token
     */
    private final static String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = "csrf_token";
    private final static int MAX_TOKEN_NUM = 5;
    private HttpSessionStore httpSessionStore;
    /**
     * csrf过期时间，默认10分钟。
     */
    private int csrfExpires=10;

    public String getTokenInSession(String sessionId) {
        synchronized (sessionId) {
            LinkedList<String> tokens = getTokensInSession(sessionId);
            String token = UUID.randomUUID().toString();
            tokens.addLast(token);
            while (tokens.size() > MAX_TOKEN_NUM) {
                tokens.removeFirst();
            }
            setTokensInSession(sessionId, tokens);
            return tokens.getLast();
        }
    }

    public void setTokensInSession(String sessionId, List<String> tokens) {
        synchronized (sessionId) {
            if (tokens.isEmpty()) {
                httpSessionStore.remove(sessionId, CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
            } else {
                httpSessionStore.setValue(sessionId, CSRF_TOKEN_FOR_SESSION_ATTR_NAME, Joiner.on(CSRF_TOKEN_SEPARATOR).join(tokens));
            }
        }
    }

    public String getTokenInRequest(HttpServletRequest request) {
        return request.getParameter(CSRF_PARAM_NAME);
    }

    public void destroyTokenInSession(String sessionId, String token) {
        synchronized (sessionId) {
            LinkedList<String> tokens = getTokensInSession(sessionId);
            tokens.remove(token);
            setTokensInSession(sessionId, tokens);
        }
    }

    public LinkedList<String> getTokensInSession(String sessionId) {
        if (null == sessionId) {
            return Lists.newLinkedList();
        } else {
            String csrf = httpSessionStore.getValue(sessionId,
                    CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
            if (null == csrf) {
                return Lists.newLinkedList();
            } else {
                return Lists.newLinkedList(Splitter.on(CSRF_TOKEN_SEPARATOR).split(httpSessionStore.getValue(sessionId,
                        CSRF_TOKEN_FOR_SESSION_ATTR_NAME)));
            }
        }
    }

    public void setHttpSessionStore(HttpSessionStore httpSessionStore) {
        this.httpSessionStore = httpSessionStore;
    }

    void setCsrfExpires(int csrfExpires) {
        this.csrfExpires = csrfExpires;
    }
}
