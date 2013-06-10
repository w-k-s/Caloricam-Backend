package com.wks.calorieapp.models;

import org.json.simple.JSONObject;


public class Response implements JSONWriteable
{
    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    
    private boolean successful;
    private String message;
    
    public Response(boolean successful, String message)
    {
	this.successful = successful;
	this.message = message;
    }
    
    public boolean isSuccessful()
    {
	return successful;
    }
    
    public void setSuccessful(boolean successful)
    {
	this.successful = successful;
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
	response.put(SUCCESS_KEY, successful);
	response.put(MESSAGE_KEY, message);
	return response.toJSONString();
    }

}
