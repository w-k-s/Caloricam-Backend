package com.wks.calorieapp.api.fatsecret.factories;

import org.json.simple.JSONObject;

import com.wks.calorieapp.api.fatsecret.entities.FSResponse;

public abstract class FSAbstractResponseFactory
{
    public abstract FSResponse createResponseFromJSON(JSONObject json);
}
