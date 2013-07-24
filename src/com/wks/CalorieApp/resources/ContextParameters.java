package com.wks.calorieapp.resources;

public enum ContextParameters
{
    USERNAME("username"),
    PASSWORD("password"),
    CONSUMER_KEY("consumer_key"),
    CONSUMER_SECRET("consumer_secret"),
    DEFAULT_MAX_HITS("default_max_hits"),
    DEFAULT_MAX_RESULTS("default_max_results"),
    ACTION("action"),
    IMAGE("img"), 
    DEFAULT_MIN_SIMILARITY("default_min_similarity");
    
    private final String value;
    
    ContextParameters(String value)
    {
	this.value = value;
    }
    
    @Override
    public String toString() {
        
        return value;
    }
}
