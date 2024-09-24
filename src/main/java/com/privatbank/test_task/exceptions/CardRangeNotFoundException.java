package com.privatbank.test_task.exceptions;

public class CardRangeNotFoundException extends RuntimeException{
    public CardRangeNotFoundException(String message) {
        super(message);
    }
}
