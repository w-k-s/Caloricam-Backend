package com.wks.calorieapp.api.fatsecret.entities;

public enum Parameter {
    SEARCH_EXPRESSION("search_expression"),
    OAUTH_CONSUMER_KEY("oauth_consumer_key"),
    OAUTH_SIGNATURE_METHOD("oauth_signature_method"),
    OAUTH_TIMESTAMP("oauth_timestamp"),
    OAUTH_NONCE("oauth_nonce"),
    OAUTH_VERSION("oauth_version"),
    OAUTH_SIGNATURE("oauth_signature"),
    METHOD("method"),
    FORMAT("format"),
    MAX_RESULTS("max_results"),
    FOOD_ID("food_id");
    
    private String name;
    
    private Parameter(String name)
    {
	this.name = name;
    }
    
    public String getName() {
	return name;
    }
    
    public String toString(){
	return name;
    }
}
