package com.wks.CalorieApp.Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import api.fatsecret.platform.Models.FatSecretAPI;
import api.fatsecret.platform.Models.FatSecretException;
import api.fatsecret.platform.Models.FoodInfoItem;
import api.fatsecret.platform.Models.FoodInfoItemFactory;


public class NutritionInfo extends HttpServlet
{

    private static final String CONTENT_TYPE	  = "application/json";
    private static final String PARAMETER_SEPERATOR   = "/";
    private static final String PARAM_CONSUMER_KEY    = "consumer_key";
    private static final String PARAM_CONSUMER_SECRET = "consumer_secret";

    private static String       consumerKey;
    private static String       consumerSecret;

    public void init() throws ServletException {
	consumerKey = getServletContext().getInitParameter(PARAM_CONSUMER_KEY);
	consumerSecret = getServletContext().getInitParameter(
		PARAM_CONSUMER_SECRET);
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	doPost(req, resp);
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();
	String[] parameters = null;
	int numResults = -1;

	if (req.getPathInfo() == null)
	{
	    outputJSON(out, false,
		    NutritionInfoStatusCode.TOO_FEW_ARGS.getMessage());
	    return;
	}

	parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

	if (parameters.length < 2)
	{
	    outputJSON(out, false,
		    NutritionInfoStatusCode.TOO_FEW_ARGS.getMessage());
	    return;
	}

	if (parameters.length > 2)
	{
	    try
	    {
		numResults = Integer.parseInt(parameters[2]);
	    } catch (NumberFormatException nfe)
	    {
		outputJSON(out, false,
			NutritionInfoStatusCode.INVALID_NUM_RESULTS_ARG
				.getMessage());
		return;
	    }
	}

	String foodName = parameters[1];

	if (consumerKey == null && consumerSecret == null)
	{
	    outputJSON(out, false,
		    NutritionInfoStatusCode.KEY_NOT_PROVIDED.getMessage());
	    return;
	}

	FatSecretAPI fatSecret = new FatSecretAPI(consumerKey, consumerSecret);
	try
	{
	    String foodInfoJson = fatSecret.foodsSearch(foodName);
	    List<FoodInfoItem> foodsInfo = FoodInfoItemFactory
		    .createFoodItemsFromJSON(foodInfoJson);

	    if (numResults == -1) numResults = foodsInfo.size();
	    JSONArray foodsJSON = new JSONArray();
	    for (int i = 0; i < numResults; i++)
	    {
		JSONObject foodJSON = new JSONObject();
		foodJSON.put("name", foodsInfo.get(i).getName());
		foodJSON.put("type", foodsInfo.get(i).getType());
		foodJSON.put("calories", foodsInfo.get(i).getCaloriesPer100g());
		foodJSON.put("fat", "" + foodsInfo.get(i).getFatPer100g());
		foodJSON.put("proteins", ""
			+ foodsInfo.get(i).getGramProteinsPer100g());
		foodJSON.put("carbohydrates", ""
			+ foodsInfo.get(i).getGramCarbsPer100g());
		foodsJSON.add(foodJSON);
	    }

	    outputJSON(out, true, foodsJSON.toJSONString());

	} catch (FatSecretException e)
	{
	    outputJSON(out, false,
		    NutritionInfoStatusCode.INFO_NOT_RETRIEVABLE.getMessage()
			    + " Error Code: " + e.getCode());
	} catch (ParseException e)
	{
	    outputJSON(out, false,
		    NutritionInfoStatusCode.INFO_NOT_RETRIEVABLE.getMessage());
	    e.printStackTrace();
	}

    }

    private void outputJSON(PrintWriter out, boolean success, String message) {
	JSONObject json = new JSONObject();
	json.put("success", success);
	json.put("message", message);

	out.println(json.toJSONString());

    }

    public enum NutritionInfoStatusCode
    {
	TOO_FEW_ARGS(
		"Insufficient arguments provided.Arguments are /nutrition_info/{required: food name/{optional: num results}};"), INVALID_NUM_RESULTS_ARG(
		"The number of results argument must be an integer. "), KEY_NOT_PROVIDED(
		"Fatal: Nutritional Information could not be retrieved because key wasn't provided. Report this error."), INFO_NOT_RETRIEVABLE(
		"Nutrition info can't be retrieved right now.");

	private final String message;

	NutritionInfoStatusCode(String message)
	{
	    this.message = message;
	}

	public String getMessage() {
	    return message;
	}
    }
}