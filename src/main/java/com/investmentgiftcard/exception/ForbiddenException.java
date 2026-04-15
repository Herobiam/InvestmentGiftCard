package com.investmentgiftcard.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user doesn't have permission to access a resource.
 */
public class ForbiddenException extends BaseException {

    private static final String ERROR_CODE = "FORBIDDEN";

    public ForbiddenException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String resourceName, String action) {
        super(
            String.format("You don't have permission to %s this %s", action, resourceName),
            ERROR_CODE,
            HttpStatus.FORBIDDEN
        );
    }
}
