package com.qumoon.commons.web.exception;

public class RedirectLocationRejectedException extends ResponseHeaderRejectedException {

  private static final long serialVersionUID = -2667477249289081304L;

  public RedirectLocationRejectedException() {
  }

  public RedirectLocationRejectedException(String message, Throwable cause) {
    super(message, cause);
  }

  public RedirectLocationRejectedException(String message) {
    super(message);
  }

  public RedirectLocationRejectedException(Throwable cause) {
    super(cause);
  }
}
