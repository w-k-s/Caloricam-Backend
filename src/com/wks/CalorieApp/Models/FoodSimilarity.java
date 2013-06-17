package com.wks.calorieapp.models;

import org.json.simple.JSONObject;

public class FoodSimilarity implements JSONWriteable
{
	private String foodName;
	private float similarity;
	
	public FoodSimilarity()
	{
		
	}

	public String getFoodName ()
	{
		return foodName;
	}
	
	public void setFoodName ( String foodName )
	{
		this.foodName = foodName;
	}
	
	public float getSimilarity ()
	{
		return similarity;
	}
	
	public void setSimilarity ( float similarity )
	{
		this.similarity = similarity;
	}
	
	public String toString()
	{
		return String.format ( "[name: %s,similarity: %f]",this.foodName, this.similarity);
	}

	@Override
	public String toJSON()
	{
	    JSONObject json = new JSONObject();
	    json.put("name",foodName);
	    json.put("similarity", similarity);
	    return json.toJSONString();
	}
}
