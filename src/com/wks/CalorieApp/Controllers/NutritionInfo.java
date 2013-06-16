package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.FatSecretAPI;
import com.wks.calorieapp.api.fatsecret.FatSecretException;
import com.wks.calorieapp.api.fatsecret.FoodInfoItem;
import com.wks.calorieapp.api.fatsecret.FoodInfoItemFactory;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class NutritionInfo extends HttpServlet
{

    private static final long serialVersionUID = 2084144039896224805L;
    private static final String CONTENT_TYPE = "application/json";
    private static final int MIN_NUM_PARAMETERS = 2;
    private static Logger logger = Logger.getLogger(NutritionInfo.class);

    private static String consumerKey;
    private static String consumerSecret;

    public void init() throws ServletException
    {
	consumerKey = getServletContext().getInitParameter(Parameter.CONSUMER_KEY.toString());
	consumerSecret = getServletContext().getInitParameter(Parameter.CONSUMER_SECRET.toString());
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	int numResults = -1;
	String foodName = "";
	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters == null || parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(false, Status.TOO_FEW_ARGS.getMessage()).toJSON());
	    return;
	} else
	{
	    if (parameters.length > 1) foodName = parameters[1];
	    if (parameters.length > 2)
	    {
		try
		{
		    numResults = Integer.parseInt(parameters[2]);
		} catch (NumberFormatException e)
		{
		    out.println(new Response(false, Status.INVALID_NUM_RESULTS_ARG.getMessage()).toJSON());
		    logger.error("Identify Request. Invalid number of maximum Hits provided: " + parameters[2], e);
		    return;
		}
	    }

	}

	logger.info("Nutrition Info Request. Finding Nutrition information for " + foodName);

	if (consumerKey == null && consumerSecret == null)
	{
	    out.println(new Response(false, Status.KEY_NOT_PROVIDED.getMessage()).toJSON());
	    logger.fatal("Nutrition Info Request. " + Status.KEY_NOT_PROVIDED.getMessage().toString());
	    return;
	}

	try
	{
	    String nutritionInfo = getNutritionInfo(foodName, numResults);
	    out.println(new Response(true, nutritionInfo).toJSON());
	} catch (FatSecretException e)
	{
	    logger.error("Nutrition Info Request. FatSecretException encountered for " + foodName, e);
	    out.println(new Response(false, Status.DATA_IRRETRIEVABLE.getMessage() + " Error Code: " + e.getCode())
		    .toJSON());

	} catch (ParseException e)
	{
	    logger.error("Nutrition Info Request. JSONParser failed to parse JSON: " + foodName, e);
	    out.println(new Response(false, Status.DATA_IRRETRIEVABLE.getMessage()).toJSON());

	} catch (IOException e)
	{
	    logger.error(
		    "Nutrition Info Request. IOException encontered while retrieving information for: " + foodName, e);
	    out.println(new Response(false, Status.IO_ERROR.getMessage()).toJSON());

	}

    }

    @SuppressWarnings("unchecked")
    private String getNutritionInfo(String foodName, int numResults) throws FatSecretException, ParseException,
	    IOException
    {
	FatSecretAPI fatSecret = new FatSecretAPI(consumerKey, consumerSecret);

	String foodJSON = fatSecret.foodsSearch(foodName);
	List<FoodInfoItem> foodList = FoodInfoItemFactory.createFoodItemsFromJSON(foodJSON);

	if (foodList == null)
	{
	    logger.info("Nutrition Info Request. No nutrition information was retrieved for: " + foodName);
	    return "";
	}

	//REFACTOR THIS CODE:
	if (numResults == -1) numResults = foodList.size();
	
	JSONArray foodsArray = new JSONArray();
	for (int i = 0; i < numResults; i++)
	{
	    JSONObject food = new JSONObject();
	    food.put("name", foodList.get(i).getName());
	    food.put("type", foodList.get(i).getType());
	    food.put("calories", foodList.get(i).getCaloriesPer100g());
	    food.put("fat", "" + foodList.get(i).getFatPer100g());
	    food.put("proteins", "" + foodList.get(i).getGramProteinsPer100g());
	    food.put("carbohydrates", "" + foodList.get(i).getGramCarbsPer100g());
	    foodsArray.add(food);
	}

	logger.info("Nutrition Info Request. Nutrition information for " + foodName + " provided.");
	return foodsArray.toJSONString();

    }

    enum Status
    {
	TOO_FEW_ARGS(
		"Insufficient arguments provided.Arguments are /nutrition_info/{required: food name/{optional: num results}};"),
	INVALID_NUM_RESULTS_ARG("The number of results argument must be an integer. "),
	KEY_NOT_PROVIDED(
		"Fatal: Nutritional Information could not be retrieved because key wasn't provided. Report this error."),
	IO_ERROR("Failed to read nutrition information from webservice."),
	DATA_IRRETRIEVABLE("Nutrition info can't be retrieved right now."),
	DATA_UNAVAILABLE("Sorry, there is no nutrition information available for this item.");

	private final String message;

	Status(String message)
	{
	    this.message = message;
	}

	public String getMessage()
	{
	    return message;
	}
    }
}
