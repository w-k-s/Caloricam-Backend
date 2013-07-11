package com.wks.calorieapp.entities;

import org.json.simple.JSONObject;


public class Response implements JSONWriteable
{
    private static final String KEY_RESPONSE_CODE = "code";
    private static final String KEY_MESSAGE = "message";
    
    private int responseCode;
    private String message;
    
    public Response(StatusCode code)
    {
	this(code,code.getDescription());
    }
    
    public Response(StatusCode code, String message)
    {
	this.responseCode = code.getCode();
	this.message = message;
    }
    
    public int getResponseCode()
    {
	return responseCode;
    }
    
    public void setResponseCode(int responseCode)
    {
	this.responseCode = responseCode;
    }
    
    public String getMessage()
    {
	return message;
    }
    
    public void setMessage(String message)
    {
	this.message = message;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String toJSON()
    {
	JSONObject response = new JSONObject();
	response.put(KEY_RESPONSE_CODE, responseCode);
	response.put(KEY_MESSAGE, message);
	return response.toJSONString();
    }

}
