package com.wks.calorieapp.services;

public class ServiceException extends Exception {

    private ErrorCodes errorCode;

    public ServiceException(ErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCodes getError() {
        return errorCode;
    }
}
