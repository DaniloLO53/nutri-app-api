package org.nutri.app.nutri_app_api.exceptions;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException(String message, Exception e) {
        super(message, e);
    }
}
