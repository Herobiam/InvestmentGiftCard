package com.investmentgiftcard.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails or is required.
 */
public class UnauthorizedException extends BaseException {

    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED, cause);
    }
}
