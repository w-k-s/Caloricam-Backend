package com.wks.calorieapp.api.fatsecret;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.entities.FSFoods;
import com.wks.calorieapp.api.fatsecret.entities.FSAbstractResponse;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;
import com.wks.calorieapp.api.fatsecret.factories.FSAbstractResponseFactory;
import com.wks.calorieapp.api.fatsecret.factories.FSResponseFactoryProducer;

public class FSWebService
{
    private FatSecretAPI apiLayer;
    private static final int NUM_TRIES = 3;
    //private static Logger logger = Logger.getLogger(FSWebService.class);

    /**Constructor
     * 
     * @param consumerKey private key for FatSecret REST API
     * @param sharedKey public key for FatSecret REST API
     */
    public FSWebService(String consumerKey, String sharedKey)
    {
	this.apiLayer = new FatSecretAPI(consumerKey, sharedKey);
    }

    /**Searches for food in FatSecret food database
     * 
     * @param foodName food name
     * @return list of nutrition info for matching items
     * @throws IOException
     * @throws ParseException
     */
    public List<NutritionInfo> searchFood(String foodName) throws IOException, ParseException
    {
	//Weird Problem:
	//Due to the nonce, the OAuth signature isn't always accepted.
	//Multiple tries need to be made. 
	List<NutritionInfo> nutritionInfoList = new ArrayList<NutritionInfo>();
	for (int i = 0; i < NUM_TRIES; i++)
	{
	    String json = this.apiLayer.foodsSearch(foodName);
	    FSAbstractResponseFactory factory = FSResponseFactoryProducer.getFactory(json);

	    FSAbstractResponse response = factory.createResponseFromJSON(json);
	    if(response instanceof FSFoods)
	    {
		nutritionInfoList = ((FSFoods) response).getNutritionInfoList();
		break;
	    }
	}
	
	return nutritionInfoList;
    }
}