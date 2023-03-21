package com.pikit.shared.exceptions;

public class InvalidRequestException extends SlickPicksException {
    public InvalidRequestException(String errorMessage) {
        super(errorMessage, 400);
    }
}
