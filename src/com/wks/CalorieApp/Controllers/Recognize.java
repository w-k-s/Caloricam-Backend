package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.FSWebService;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;
import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.entities.StatusCode;
import com.wks.calorieapp.services.Identifier;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class Recognize extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final String ARG_FORMAT = "/{string: imagename (required) }/{float: minSimilarity(optional)}/{int: maxhits(optional)}";
    private static final String CONTENT_TYPE = "application/json";

    private static final int MIN_NUM_PARAMETERS = 2;
    private static final int DEFAULT_MAX_HITS = 10;
    private static final float DEFAULT_MIN_SIMILARITY = 0F;

    private static Connection connection = null;

    private static String imagesDir = "";
    private static String indexesDir = "";
    private static int defaultMaxHits = DEFAULT_MAX_HITS;
    private static float defaultMinSimilarity = DEFAULT_MIN_SIMILARITY;

    private static String consumerKey;
    private static String consumerSecret;

    private static Logger logger = Logger.getLogger(Identify.class);

    @Override
    public void init() throws ServletException
    {
	imagesDir = Environment.getImagesDirectory(getServletContext());
	indexesDir = Environment.getIndexesDirectory(getServletContext());
	defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(Parameter.DEFAULT_MAX_HITS.toString()));
	defaultMinSimilarity = Float.parseFloat(getServletContext().getInitParameter(
		Parameter.DEFAULT_MIN_SIMILARITY.toString()));

	consumerKey = getServletContext().getInitParameter(Parameter.CONSUMER_KEY.toString());
	consumerSecret = getServletContext().getInitParameter(Parameter.CONSUMER_SECRET.toString());

	connection = DatabaseUtils.getConnection();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	// TODO Auto-generated method stub
	doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	String fileName = "";
	int maximumHits = defaultMaxHits;
	float minimumSimilarity = defaultMinSimilarity;

	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters == null || parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS, StatusCode.TOO_FEW_ARGS.getDescription(ARG_FORMAT))
		    .toJSON());
	    return;
	}

	try
	{
	    if (parameters.length > 1) fileName = parameters[1];
	    if (parameters.length > 2) minimumSimilarity = Float.parseFloat(parameters[2]);
	    if (parameters.length > 3) maximumHits = Integer.parseInt(parameters[3]);
	} catch (NumberFormatException e)
	{
	    out.println(new Response(StatusCode.INVALID_ARG, StatusCode.INVALID_ARG.getDescription(ARG_FORMAT))
		    .toJSON());
	    logger.error("Recognise Request. Invalid number of maximum Hits provided: " + parameters[2], e);
	    return;
	}

	// check that file exists
	String fileURI = imagesDir + fileName;

	logger.info("Recognise Request. Image: " + fileURI + " maximumHits: " + maximumHits);

	Map<String, Float> foodNameSimilarity = getSimilarFoods(fileURI, indexesDir, minimumSimilarity, maximumHits);

	Map<String, List<NutritionInfo>> nutritionInfo = getNutritionInfoForFoods(foodNameSimilarity.keySet());
	out.println(new Response(StatusCode.OK, JSONValue.toJSONString(nutritionInfo)).toJSON());

    }

    private Map<String, Float> getSimilarFoods(String fileURI, String indexesDir, float minimumSimilarity,
	    int maximumHits)
    {
	Map<String, Float> foodNameSimilarity = new HashMap<String, Float>();
	try
	{
	    Identifier identifier = Identifier.getInstance(connection);
	    foodNameSimilarity = identifier.getPossibleFoodsForImage(fileURI, indexesDir, minimumSimilarity,
		    maximumHits);

	} catch (DataAccessObjectException e)
	{

	    logger.error("Recognise request. Failure to load food name from database", e);

	} catch (IOException e)
	{
	    logger.error("Recognise request. IO Exception encountered while recognising.", e);
	}

	return foodNameSimilarity;
    }

    private Map<String, List<NutritionInfo>> getNutritionInfoForFoods(Set<String> foodNames)
    {
	Map<String, List<NutritionInfo>> nutritionInfo = new HashMap<String, List<NutritionInfo>>();
	for (String foodName : foodNames)
	{
	    try
	    {
		FSWebService fsWebService = new FSWebService(consumerKey, consumerSecret);
		List<NutritionInfo> info = fsWebService.searchFood(foodName);
		if (info != null)
		{
		    nutritionInfo.put(foodName, info);
		}
	    } catch (ParseException e)
	    {
		logger.error("Nutrition Info Request. JSONParser failed to parse JSON: " + foodName, e);

	    } catch (IOException e)
	    {
		logger.error("Nutrition Info Request. IOException encontered while retrieving information for: "
			+ foodName, e);

	    }
	}

	return nutritionInfo;
    }

}
