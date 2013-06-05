package com.wks.CalorieApp.Utils;

import org.json.simple.JSONObject;

public class JSONHelper
{

    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    
    @SuppressWarnings("unchecked")
    public static String writeStatus(boolean success, String message)
    {
	JSONObject status = new JSONObject();
	status.put(SUCCESS_KEY, success);
	status.put(MESSAGE_KEY, message);
	return status.toJSONString();
    }
    
}
