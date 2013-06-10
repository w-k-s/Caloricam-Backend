package com.wks.calorieapp.api.fatsecret;

public enum Method {
    FOODS_SEARCH("foods.search"),
    FOOD_GET("food.get");
    
    private final String name;
    
    private Method(String name)
    {
	this.name = name;
    }
    
    public String getName() {
	return name;
    }
    
    public String toString()
    {
	return name;
    }
}
