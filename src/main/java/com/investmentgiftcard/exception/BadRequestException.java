package com.investmentgiftcard.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a request is malformed or contains invalid data.
 */
public class BadRequestException extends BaseException {

    private static final String ERROR_CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, String details) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST, details);
    }
}
