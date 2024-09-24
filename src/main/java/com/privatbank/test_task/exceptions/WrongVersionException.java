package com.privatbank.test_task.exceptions;

public class WrongVersionException extends RuntimeException{
    public WrongVersionException(String message) {
        super(message);
    }
}
