package com.wks.calorieapp.services.fatsecret.factories;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FSResponseFactoryProducer
{
    private static final String JSON_FOODS_KEY = "foods";
    private static final String JSON_ERROR_KEY = "error";
    
    public static FSAbstractResponseFactory getFactory(String json) throws ParseException
    {
	JSONParser parser = new JSONParser();
	Object object = parser.parse(json);
	JSONObject jsonObject = (JSONObject) object;
	
	
	if(jsonObject.get(JSON_FOODS_KEY) != null)
	{
	    return new FSFoodsResponseFactory( );
	}else if(jsonObject.get(JSON_ERROR_KEY) != null)
	{
	    return new FSErrorResponseFactory( );
	}else
	    return null;
    }
}
