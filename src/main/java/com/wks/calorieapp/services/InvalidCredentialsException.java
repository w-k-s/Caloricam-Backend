package com.wks.calorieapp.services;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("invalid username/password");
    }
}
