package com.investmentgiftcard.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there's a conflict with the current state of a resource.
 */
public class ConflictException extends BaseException {

    private static final String ERROR_CODE = "CONFLICT";

    public ConflictException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT);
    }

    public ConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(
            String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            ERROR_CODE,
            HttpStatus.CONFLICT
        );
    }
}
