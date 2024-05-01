package com.example.testassignment.exceptions;

public class RestResponseException extends RuntimeException{
    public RestResponseException() {
        super();
    }
    public RestResponseException(String message, Throwable cause) {
        super(message, cause);
    }
    public RestResponseException(String error) {
        super(error);
    }
    public RestResponseException(Throwable cause) {
        super(cause);
    }
}
