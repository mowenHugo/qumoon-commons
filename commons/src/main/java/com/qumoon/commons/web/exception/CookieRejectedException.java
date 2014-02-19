package com.qumoon.commons.web.exception;

/**
 * @author kevin
 */
public class CookieRejectedException extends ResponseHeaderRejectedException {
    private static final long serialVersionUID = -2667477249289081304L;

    public CookieRejectedException() {
    }

    public CookieRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieRejectedException(String message) {
        super(message);
    }

    public CookieRejectedException(Throwable cause) {
        super(cause);
    }
}
