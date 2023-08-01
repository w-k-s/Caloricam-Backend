package com.wks.calorieapp.services;

public class ServiceException extends Exception {

    private ErrorCodes errorCode;

    public ServiceException(ErrorCodes errorCode) {
        this(errorCode, errorCode.getDescription());
    }

    public ServiceException(ErrorCodes errorCode, String message) {
        this(errorCode, message, null);
    }

    public ServiceException(ErrorCodes errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCodes getError() {
        return errorCode;
    }
}
