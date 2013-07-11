package com.wks.calorieapp.api.fatsecret.factories;

import org.json.simple.JSONObject;

import com.wks.calorieapp.api.fatsecret.entities.FSError;
import com.wks.calorieapp.api.fatsecret.entities.FSResponse;

public class FSErrorResponseFactory extends FSAbstractResponseFactory
{
    private static final String JSON_ERROR_KEY = "error";
    private static final String JSON_ERROR_CODE_KEY = "code";
    private static final String JSON_ERROR_MESSAGE_KEY = "message";
    
    public FSErrorResponseFactory()
    {
	
    }

   
    public FSResponse createResponseFromJSON(JSONObject responseJson)
    {
	JSONObject errorJson = (JSONObject) responseJson.get(JSON_ERROR_KEY);
	long code = (Long) errorJson.get(JSON_ERROR_CODE_KEY) | 0L;
	String message = (String) errorJson.get(JSON_ERROR_MESSAGE_KEY);
	return new FSError(code, message);
    }

}
