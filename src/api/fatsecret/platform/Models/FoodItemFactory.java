package api.fatsecret.platform.Models;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FoodItemFactory {
    
    private static JSONParser parser;
    
    //TODO make static literals
    
    public static List<FoodItem> createFoodItemFromJSON(String jsonString) throws FatSecretException, ParseException
    {
	parser = new JSONParser();
	JSONObject json = (JSONObject) parser.parse(jsonString);
	
	JSONObject foodsJSON = (JSONObject) json.get("foods");
	JSONObject errorJSON = (JSONObject) json.get("error");
	
	if(foodsJSON==null && errorJSON == null)
	    throw new FatSecretException("No response received");
	
	if(errorJSON!=null)
	    throw createFatSecretException(errorJSON);
	
	
	List<FoodItem> foods = new ArrayList<FoodItem>();
	JSONArray foodJSON = (JSONArray) json.get("food");
	
	if(foodJSON != null)
	{
	  //iterate over foods, create item and add to list.
		
	}
	
	
	return foods;
    }

    private static FatSecretException createFatSecretException(JSONObject errorJSON) {
	
	return null;
    }
}
