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
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.FatSecretAPI;
import com.wks.calorieapp.api.fatsecret.FatSecretException;
import com.wks.calorieapp.api.fatsecret.NutritionInfo;
import com.wks.calorieapp.api.fatsecret.NutritionInfoFactory;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.models.StatusCode;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class GetNutritionInfo extends HttpServlet
{

    private static final long serialVersionUID = 2084144039896224805L;
    private static final String ARG_FORMAT = "/{string: foodname (required) }/{int: numResults(optional)}";
    private static final String CONTENT_TYPE = "application/json";
    private static final int MIN_NUM_PARAMETERS = 2;
    private static Logger logger = Logger.getLogger(GetNutritionInfo.class);

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
	    out.println(new Response(StatusCode.TOO_FEW_ARGS,ARG_FORMAT).toJSON());
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
		    out.println(new Response(StatusCode.INVALID_ARG,ARG_FORMAT).toJSON());
		    logger.error("Identify Request. Invalid number of maximum Hits provided: " + parameters[2], e);
		    return;
		}
	    }

	}

	logger.info("Nutrition Info Request. Finding Nutrition information for " + foodName);

	if (consumerKey == null && consumerSecret == null)
	{
	    out.println(new Response(StatusCode.NULL_KEY).toJSON());
	    logger.fatal("Nutrition Info Request. " + StatusCode.NULL_KEY.getDescription());
	    return;
	}

	try
	{
	    String nutritionInfo = getNutritionInfo(foodName, numResults);
	    out.println(new Response(StatusCode.OK, nutritionInfo).toJSON());
	} catch (FatSecretException e)
	{
	    logger.error("Nutrition Info Request. FatSecretException encountered for " + foodName, e);
	    out.println(new Response(StatusCode.FAT_SECRET_ERROR, StatusCode.FAT_SECRET_ERROR.getDescription(""+e.getCode())).toJSON());

	} catch (ParseException e)
	{
	    logger.error("Nutrition Info Request. JSONParser failed to parse JSON: " + foodName, e);
	    out.println(new Response(StatusCode.PARSE_ERROR, StatusCode.PARSE_ERROR.getDescription(foodName)).toJSON());

	} catch (IOException e)
	{
	    logger.error(
		    "Nutrition Info Request. IOException encontered while retrieving information for: " + foodName, e);
	    out.println(new Response(StatusCode.FILE_IO_ERROR, StatusCode.FILE_IO_ERROR.getDescription(e.toString())).toJSON());

	}

    }

    @SuppressWarnings("unchecked")
    private String getNutritionInfo(String foodName, int numResults) throws FatSecretException, ParseException,
	    IOException
    {
	FatSecretAPI fatSecret = new FatSecretAPI(consumerKey, consumerSecret);

	String foodJSON = fatSecret.foodsSearch(foodName);
	List<NutritionInfo> foodList = NutritionInfoFactory.createNutritionInfoFromJSON(foodJSON);

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
	    foodsArray.add(foodList.get(i).toJSON());
	}

	logger.info("Nutrition Info Request. Nutrition information for " + foodName + " provided.");
	return foodsArray.toJSONString();

    }

}
