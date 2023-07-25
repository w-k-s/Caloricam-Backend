package com.wks.calorieapp.api.fatsecret.factories;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.entities.FSError;

public class FSErrorResponseFactory extends FSAbstractResponseFactory
{
    private static final String JSON_ERROR_KEY = "error";
    private static final String JSON_ERROR_CODE_KEY = "code";
    private static final String JSON_ERROR_MESSAGE_KEY = "message";
    
    public FSErrorResponseFactory()
    {
	
    }

   
    public FSError createResponseFromJSON(String json) throws ParseException
    {
	JSONParser parser = new JSONParser();
	JSONObject responseJson = (JSONObject) parser.parse(json);
	
	JSONObject errorJson = (JSONObject) responseJson.get(JSON_ERROR_KEY);
	long code = (Long) errorJson.get(JSON_ERROR_CODE_KEY) | 0L;
	String message = (String) errorJson.get(JSON_ERROR_MESSAGE_KEY);
	return new FSError(code, message);
    }

}
