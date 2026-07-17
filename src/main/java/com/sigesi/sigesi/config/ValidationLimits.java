package com.sigesi.sigesi.config;

/**
 * Shared limits for user-provided text.
 */
public final class ValidationLimits {

  public static final int SEARCH = 100;
  public static final int CPF = 14;
  public static final int CODE = 50;
  public static final int SHORT_TEXT = 150;
  public static final int ADDRESS = 255;
  public static final int LONG_TEXT = 5000;
  public static final int DOCUMENT_BODY = 20000;

  private ValidationLimits() {
    // Constants only.
  }
}
