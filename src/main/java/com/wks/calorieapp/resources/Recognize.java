package com.wks.calorieapp.resources;

import java.io.File;
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
import com.wks.calorieapp.services.Identifier;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;

public class Recognize extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";

    private static final String PARAM_IMAGE_NAME = "image_name";
    private static final String PARAM_MIN_SIMILARITY = "min_similarity";
    private static final String PARAM_MAX_HITS = "max_hits";
  
    private static Connection connection = null;
    private static String imagesDir = "";
    private static String indexesDir = "";
    private static String consumerKey;
    private static String consumerSecret;
    private static int defaultMaxHits;
    private static float defaultMinSimilarity;

    private static Logger logger = Logger.getLogger(Identify.class);

    @Override
    public void init() throws ServletException
    {
	imagesDir = Environment.getImagesDirectory(getServletContext());
	indexesDir = Environment.getIndexesDirectory(getServletContext());
	defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(ContextParameters.DEFAULT_MAX_HITS.toString()));
	defaultMinSimilarity = Float.parseFloat(getServletContext().getInitParameter(
		ContextParameters.DEFAULT_MIN_SIMILARITY.toString()));

	consumerKey = getServletContext().getInitParameter(ContextParameters.CONSUMER_KEY.toString());
	consumerSecret = getServletContext().getInitParameter(ContextParameters.CONSUMER_SECRET.toString());

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

	String imageName = req.getParameter(PARAM_IMAGE_NAME);
	float minSimilarity = req.getParameter(PARAM_MIN_SIMILARITY)==null?defaultMinSimilarity:Float.valueOf(req.getParameter(PARAM_MIN_SIMILARITY));
	int maximumHits = req.getParameter(PARAM_MAX_HITS)==null?defaultMaxHits : Integer.valueOf(req.getParameter(PARAM_MAX_HITS));

	if (imageName == null)
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS)
		    .toJSON());
	    return;
	}

	File imageFile = new File(imagesDir + imageName);
	if (!imageFile.exists())
	{
	    out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
	    logger.error("Index Request Failed. " + imageName + " does not exist.");
	    return;
	}

	logger.info("Recognise Request. Image: " + imageFile.getAbsolutePath() + " maximumHits: " + maximumHits);

	Map<String, Float> foodNameSimilarity = getSimilarFoods(imageFile.getAbsolutePath(), indexesDir, minSimilarity, maximumHits);

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
