package com.wks.calorieapp.api.fatsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NutritionInfoFactory
{
    private static final String JSON_FOODS_KEY = "foods";
    private static final String JSON_FOOD_KEY = "food";
    private static final String JSON_FOOD_ID_KEY = "food_id";
    private static final String JSON_FOOD_NAME_KEY = "food_name";
    private static final String JSON_FOOD_TYPE_KEY = "food_type";
    private static final String JSON_FOOD_DESCRIPTION_KEY = "food_description";
    private static final String JSON_FOOD_URL_KEY = "food_url";

    private static final String JSON_ERROR_KEY = "error";
    private static final String JSON_ERROR_CODE_KEY = "code";
    private static final String JSON_ERROR_MESSAGE_KEY = "message";

    private static JSONParser parser;

    public static List<NutritionInfo> createNutritionInfoFromJSON(String jsonString) throws FatSecretException,
	    ParseException {
	parser = new JSONParser();
	JSONObject json = (JSONObject) parser.parse(jsonString);

	JSONObject foodsJSON = (JSONObject) json.get(JSON_FOODS_KEY);
	JSONObject errorJSON = (JSONObject) json.get(JSON_ERROR_KEY);

	if (foodsJSON == null && errorJSON == null) return null;

	if (errorJSON != null) throw createFatSecretException(errorJSON);

	List<NutritionInfo> foods = new ArrayList<NutritionInfo>();
	JSONArray foodJSON = (JSONArray) foodsJSON.get(JSON_FOOD_KEY);

	if (foodJSON != null)
	{

	    @SuppressWarnings("unchecked")
	    Iterator<JSONObject> iterator = foodJSON.iterator();

	    while (iterator.hasNext())
	    {
		JSONObject foodObject = iterator.next();

		String foodId = (String) foodObject.get(JSON_FOOD_ID_KEY);
		String foodName = (String) foodObject.get(JSON_FOOD_NAME_KEY);
		String foodDescription = (String) foodObject.get(JSON_FOOD_DESCRIPTION_KEY);

		String foodType = (String) foodObject.get(JSON_FOOD_TYPE_KEY);
		String foodURL = (String) foodObject.get(JSON_FOOD_URL_KEY);

		NutritionInfo foodInfoItem = new NutritionInfo();
		foodInfoItem.setId(Long.parseLong(foodId));
		foodInfoItem.setDescription(foodDescription);
		foodInfoItem.setName(foodName);
		foodInfoItem.setType(foodType);
		foodInfoItem.setUrl(foodURL);

		foods.add(foodInfoItem);
	    }

	}

	return foods;
    }

    private static FatSecretException createFatSecretException(JSONObject errorJSON) {

	long code = (Long) errorJSON.get(JSON_ERROR_CODE_KEY) | 0L;
	String message = (String) errorJSON.get(JSON_ERROR_MESSAGE_KEY);
	return new FatSecretException(code, message);

    }
}
