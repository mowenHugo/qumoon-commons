package com.qumoon.commons.web.exception;

/**
 * @author kevin
 */
public class ResponseHeaderRejectedException extends RuntimeException {
    private static final long serialVersionUID = -5208648752795414458L;

    public ResponseHeaderRejectedException() {
    }

    public ResponseHeaderRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseHeaderRejectedException(String message) {
        super(message);
    }

    public ResponseHeaderRejectedException(Throwable cause) {
        super(cause);
    }
}
