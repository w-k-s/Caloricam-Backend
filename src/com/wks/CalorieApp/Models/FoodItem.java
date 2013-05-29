package com.wks.CalorieApp.Models;

/**
 * POJO representing Food Item in Foods database.
 * @author Waqqas
 *
 */
public class FoodItem {
    private long foodId;
    private String name;
    
    public long getFoodId() {
	return foodId;
    }
    
    public void setFoodId(long foodId) {
	this.foodId = foodId;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
   
    public String toString() {
        return String.format("[id: %d,name: %s]", foodId,name);
    }
    
}
