package com.privatbank.test_task.exceptions;

public class WrongCardNumberException extends RuntimeException{
    public WrongCardNumberException(String message) {
        super(message);
    }
}
