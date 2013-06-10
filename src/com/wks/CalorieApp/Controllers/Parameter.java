package com.wks.calorieapp.controllers;

public enum Parameter
{
    USERNAME("username"),
    PASSWORD("password"),
    CONSUMER_KEY("consumer_key"),
    CONSUMER_SECRET("consumer_secret"),
    DEFAULT_MAX_HITS("default_max_hits"),
    ACTION("action"),
    IMAGE("img");
    
    private final String value;
    
    Parameter(String value)
    {
	this.value = value;
    }
    
    @Override
    public String toString() {
        
        return value;
    }
}
