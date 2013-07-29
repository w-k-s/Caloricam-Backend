package com.wks.calorieapp.api.fatsecret.factories;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;

public class FSNutritionInfoFactory extends FSAbstractResponseFactory
{

    private static final String JSON_FOOD_ID_KEY = "food_id";
    private static final String JSON_FOOD_NAME_KEY = "food_name";
    private static final String JSON_FOOD_TYPE_KEY = "food_type";
    private static final String JSON_FOOD_DESCRIPTION_KEY = "food_description";
    private static final String JSON_FOOD_URL_KEY = "food_url";
    
    @Override
    public NutritionInfo createResponseFromJSON(String json) throws ParseException
    {
	JSONParser parser = new JSONParser();
	JSONObject nutritionInfoJson = (JSONObject) parser.parse(json);
	
	String foodId = (String) nutritionInfoJson.get(JSON_FOOD_ID_KEY);
	String foodName = (String) nutritionInfoJson.get(JSON_FOOD_NAME_KEY);
	String foodDescription = (String) nutritionInfoJson.get(JSON_FOOD_DESCRIPTION_KEY);

	String foodType = (String) nutritionInfoJson.get(JSON_FOOD_TYPE_KEY);
	String foodURL = (String) nutritionInfoJson.get(JSON_FOOD_URL_KEY);
	
	NutritionInfo info = new NutritionInfo();
	info.setId(Long.parseLong(foodId));
	info.setName(foodName);
	info.setDescription(foodDescription);
	info.setType(foodType);
	info.setUrl(foodURL);
	
	return info;
    }

  
    

}
