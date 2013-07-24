package com.wks.calorieapp.resources;

public enum Attributes
{
    STATUS("status"),
    AUTHENTICATED("authenticated"),
    USERNAME("username"),
    IMAGE_LIST("images"),
    IMAGE_DIR("image_dir"),
    INDEX_LIST("indexes");
    
    private final String value;
    
    Attributes(String value)
    {
	this.value = value;
    }
    
    @Override
    public String toString() {
       return value;
    }
}
