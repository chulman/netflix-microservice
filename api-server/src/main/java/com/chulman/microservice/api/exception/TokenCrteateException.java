package com.chulman.microservice.api.exception;

public class TokenCrteateException extends RuntimeException{
    public TokenCrteateException() {
        super();
    }

    public TokenCrteateException(String message) {
        super(message);
    }

    public TokenCrteateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenCrteateException(Throwable cause) {
        super(cause);
    }
}
