package com.wks.CalorieApp.StatusCodes;

public enum LoginStatusCodes {
    NULL_USERNAME_PASSWORD("Must provide a valid username and password."),
    INCORRECT_USERNAME_PASSWORD("Username or password is not correct."),
    NOT_REGISTERED("This username is not registered as admin.");
    
    private final String description;
    
    private LoginStatusCodes(String description)
    {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }
}
