package com.pikit.shared.exceptions;

import lombok.Getter;

@Getter
public class SlickPicksException extends Exception {
    private int errorCode;
    public SlickPicksException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
