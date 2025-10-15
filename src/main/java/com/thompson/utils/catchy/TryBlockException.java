package com.thompson.utils.catchy;

public class TryBlockException extends RuntimeException {
    public TryBlockException(String message) {
        super(message);
    }
    public TryBlockException(String message, Throwable cause) {
        super(message, cause);
    }
    public TryBlockException(Throwable cause) {
        super(cause);
    }
}
