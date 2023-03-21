package com.pikit.shared.exceptions;

public class InternalErrorException extends SlickPicksException {
    public InternalErrorException(String errorMessage) {
        super(errorMessage, 500);
    }
}
