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

import com.wks.CalorieApp.api.fatsecret.FatSecretAPI;
import com.wks.CalorieApp.api.fatsecret.FatSecretException;
import com.wks.CalorieApp.api.fatsecret.FoodInfoItem;
import com.wks.CalorieApp.api.fatsecret.FoodInfoItemFactory;
import com.wks.CalorieApp.Utils.JSONHelper;

public class NutritionInfo extends HttpServlet
{

    private static final long serialVersionUID = 2084144039896224805L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String PARAMETER_SEPERATOR = "/";
    private static final int MIN_NUM_ARGS = 2;

    private static String consumerKey;
    private static String consumerSecret;

    public void init() throws ServletException {
	consumerKey = getServletContext().getInitParameter(Parameter.CONSUMER_KEY.toString());
	consumerSecret = getServletContext().getInitParameter(Parameter.CONSUMER_SECRET.toString());
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doPost(req, resp);
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();
	String[] parameters = null;
	int numResults = -1;

	if (req.getPathInfo() == null)
	{
	    out.println(JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));
	    return;
	}

	parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

	if (parameters.length < MIN_NUM_ARGS)
	{
	    out.println(JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));
	    return;
	}

	if (parameters.length > MIN_NUM_ARGS)
	{
	    try
	    {
		numResults = Integer.parseInt(parameters[2]);
	    } catch (Exception e)
	    {
		out.println(JSONHelper.writeStatus(false, Status.INVALID_NUM_RESULTS_ARG.getMessage()));
		return;
	    }
	}

	String foodName = parameters[1];

	if (consumerKey == null && consumerSecret == null)
	{
	    out.println(JSONHelper.writeStatus(false, Status.KEY_NOT_PROVIDED.getMessage()));
	    return;
	}

	FatSecretAPI fatSecret = new FatSecretAPI(consumerKey, consumerSecret);
	try
	{
	    String foodJSON = fatSecret.foodsSearch(foodName);
	    List<FoodInfoItem> foodList = FoodInfoItemFactory.createFoodItemsFromJSON(foodJSON);

	    if (foodList == null)
	    {
		out.println( JSONHelper.writeStatus(false, Status.DATA_UNAVAILABLE.getMessage()) );
		return;
	    }

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

	    out.println(JSONHelper.writeStatus(true, foodsArray.toJSONString()));

	} catch (FatSecretException e)
	{
	    out.println(JSONHelper.writeStatus(false,
		    Status.DATA_IRRETRIEVABLE.getMessage() + " Error Code: " + e.getCode()));
	} catch (ParseException e)
	{
	    out.println(JSONHelper.writeStatus(false, Status.DATA_IRRETRIEVABLE.getMessage()));
	    e.printStackTrace();
	}

    }

    enum Status
    {
	TOO_FEW_ARGS("Insufficient arguments provided.Arguments are /nutrition_info/{required: food name/{optional: num results}};"),
	INVALID_NUM_RESULTS_ARG("The number of results argument must be an integer. "),
	KEY_NOT_PROVIDED("Fatal: Nutritional Information could not be retrieved because key wasn't provided. Report this error."),
	DATA_IRRETRIEVABLE("Nutrition info can't be retrieved right now."),
	DATA_UNAVAILABLE("Sorry, there is no nutrition information available for this item.");

	private final String message;

	Status(String message)
	{
	    this.message = message;
	}

	public String getMessage() {
	    return message;
	}
    }
}
