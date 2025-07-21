package org.nutri.app.nutri_app_api.exceptions;

public class ForbiddenException extends RuntimeException {
     public ForbiddenException(String message) {
        super(message);
    }
}
