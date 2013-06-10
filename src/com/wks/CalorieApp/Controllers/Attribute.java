package com.wks.calorieapp.controllers;

public enum Attribute
{
    STATUS("status"),
    AUTHENTICATED("authenticated"),
    USERNAME("username"),
    IMAGE_LIST("images"),
    IMAGE_DIR("image_dir");
    
    private final String value;
    
    Attribute(String value)
    {
	this.value = value;
    }
    
    @Override
    public String toString() {
       return value;
    }
}
