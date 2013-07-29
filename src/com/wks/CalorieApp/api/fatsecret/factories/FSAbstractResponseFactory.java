package com.wks.calorieapp.api.fatsecret.factories;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.entities.FSAbstractResponse;

public abstract class FSAbstractResponseFactory
{
    public abstract FSAbstractResponse createResponseFromJSON(String json) throws ParseException;
}
