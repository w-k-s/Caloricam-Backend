package com.wks.calorieapp.services.fatsecret.factories;

import org.json.simple.parser.ParseException;

import com.wks.calorieapp.services.fatsecret.entities.FSAbstractResponse;

/**
 * 
 * @author Waqqas
 *
 */
public abstract class FSAbstractResponseFactory
{
    public abstract FSAbstractResponse createResponseFromJSON(String json) throws ParseException;
}
