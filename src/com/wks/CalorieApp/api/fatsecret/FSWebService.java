package com.wks.calorieapp.api.fatsecret;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.entities.FSFoods;
import com.wks.calorieapp.api.fatsecret.entities.FSResponse;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;
import com.wks.calorieapp.api.fatsecret.factories.FSAbstractResponseFactory;
import com.wks.calorieapp.api.fatsecret.factories.FSResponseFactory;

public class FSWebService
{
    private FatSecretAPI apiLayer;
    private static final int NUM_TRIES = 3;
    //private static Logger logger = Logger.getLogger(FSWebService.class);

    public FSWebService(String consumerKey, String sharedKey)
    {
	this.apiLayer = new FatSecretAPI(consumerKey, sharedKey);
    }

    public List<NutritionInfo> searchFood(String foodName) throws IOException, ParseException
    {
	List<NutritionInfo> nutritionInfoList = new ArrayList<NutritionInfo>();
	for (int i = 0; i < NUM_TRIES; i++)
	{
	    String json = this.apiLayer.foodsSearch(foodName);
	    FSAbstractResponseFactory factory = FSResponseFactory.getFactory(json);

	    JSONParser parser = new JSONParser();
	    JSONObject responseJson = (JSONObject) parser.parse(json);

	    FSResponse response = factory.createResponseFromJSON(responseJson);
	    if(response instanceof FSFoods)
	    {
		nutritionInfoList = ((FSFoods) response).getNutritionInfoList();
		break;
	    }
	}

	
	return nutritionInfoList;
    }
}
