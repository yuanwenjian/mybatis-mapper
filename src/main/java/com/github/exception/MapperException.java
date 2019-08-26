package com.github.exception;

public class MapperException extends RuntimeException {


    public MapperException() {
        super();
    }

    public MapperException(String message) {
        super(message);
    }


    public MapperException(String message, Throwable cause) {
        super(message,cause);
    }
}
