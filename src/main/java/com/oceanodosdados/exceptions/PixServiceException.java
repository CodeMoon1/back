package com.oceanodosdados.exceptions;

public class PixServiceException extends Exception {

    public PixServiceException(String message) {
        super(message);
    }

    public PixServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
