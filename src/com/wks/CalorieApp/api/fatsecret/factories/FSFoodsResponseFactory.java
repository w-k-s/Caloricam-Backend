package com.wks.calorieapp.api.fatsecret.factories;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wks.calorieapp.api.fatsecret.entities.FSFoods;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;

public class FSFoodsResponseFactory extends FSAbstractResponseFactory
{
    
    private static final String JSON_FOODS_KEY = "foods";
    private static final String JSON_FOOD_KEY = "food";
    

    @Override
    public FSFoods createResponseFromJSON(JSONObject jsonObject)
    {
	FSFoods foods = new FSFoods();
	JSONObject foodsJson = (JSONObject)jsonObject.get(JSON_FOODS_KEY);
	JSONArray nutritionInfoJsonArray = (JSONArray) foodsJson.get(JSON_FOOD_KEY);
	
	@SuppressWarnings("unchecked")
	Iterator<JSONObject> iterator = nutritionInfoJsonArray.iterator();
	
	FSNutritionInfoFactory nutritionInfoFactory = new FSNutritionInfoFactory();
	
	while(iterator.hasNext())
	{
	    JSONObject nutritionInfoJson = iterator.next();
	    NutritionInfo nutritionInfo = nutritionInfoFactory.createResponseFromJSON(nutritionInfoJson);
	    foods.add(nutritionInfo);
	}
	    
	return foods;
    }
    
    
}
