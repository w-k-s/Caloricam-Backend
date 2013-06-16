package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDataAccessObject;
import com.wks.calorieapp.daos.ImageDataAccessObject;
import com.wks.calorieapp.models.FoodDataTransferObject;
import com.wks.calorieapp.models.Identifier;
import com.wks.calorieapp.models.ImageDataTransferObject;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.RequestParameterUtil;

/*
 * - think of a better JSON writer impmentation.
 * - create an abstract class for index writing
 * - handle duplication??
 */

public class Identify extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String SIMILAR_IMAGE_NAME_SIMILARITY_SEPERATOR = ":";

    private static final int MIN_NUM_PARAMETERS = 2;
    private static final int DEFAULT_MAX_HITS = 10;

    private static Connection connection = null;

    private static String imagesDir = "";
    private static String indexesDir = "";
    private static int defaultMaxHits = DEFAULT_MAX_HITS;
    private static Logger logger = Logger.getLogger(Identify.class);

    @Override
    public void init() throws ServletException
    {
	imagesDir = Environment.getImagesDirectory(getServletContext());
	indexesDir = Environment.getIndexesDirectory(getServletContext());
	defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(Parameter.DEFAULT_MAX_HITS.toString()));
	connection = DatabaseUtils.getConnection();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	String fileName = "";
	int maximumHits = defaultMaxHits;
	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters == null || parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(false, Status.TOO_FEW_ARGS.getMessage()).toJSON());
	    return;
	} else
	{
	    if (parameters.length > 1) fileName = parameters[1];
	    if (parameters.length > 2)
	    {
		try
		{
		    maximumHits = Integer.parseInt(parameters[2]);
		} catch (NumberFormatException e)
		{
		    out.println(new Response(false, Status.INVALID_MAX_HITS.getMessage()).toJSON());
		    logger.error("Identify Request. Invalid number of maximum Hits provided: " + parameters[2], e);
		    return;
		}
	    }

	}

	// check that file exists
	String fileURI = imagesDir + fileName;

	logger.info("Identify Request. Image: " + fileURI + " maximumHits: " + maximumHits);

	try
	{
	    Map<Float, String> similarityImageMap = getSimilarImages(fileURI, maximumHits);  
	    Map<Float, String> similarityFoodMap = new HashMap<Float, String>();
	    for (Entry<Float, String> entry : similarityImageMap.entrySet())
	    {
		String imageName = entry.getValue();
		String foodName = getFoodNameForImage(imageName);
		

		if (foodName != null && !foodName.isEmpty())
		{
		    similarityFoodMap.put(entry.getKey(), foodName);
		}

	    }

	    
	    // convert hashmap into json
	    String jsonSimilarityFoodMap = JSONValue.toJSONString(similarityFoodMap);

	    // retur resposne object.
	    out.println(new Response(true, jsonSimilarityFoodMap).toJSON());

	} catch (LireResultsParseException e)
	{
	
	    logger.error("Failure to parse LIRe image identification results.",e);
	    out.println(new Response(false,Status.IDENTIFICATION_FAILED.getMessage()).toJSON());
	} catch (DataAccessObjectException e)
	{
	    
	    logger.error("Failure to load food name from database",e);
	    out.println(new Response(false,Status.DB_ACCESS_ERROR.getMessage()).toJSON());

	}catch(IOException e)
	{
	    logger.error("IO Exception encountered while finding similar image.",e);
	    out.println(new Response(false,Status.IO_ERROR.getMessage()).toJSON());
	}

    }

    private HashMap<Float, String> getSimilarImages(String fileURI, int maximumHits) throws LireResultsParseException, IOException
    {
	// find similar images
	HashMap<Float, String> similarityImageMap = new HashMap<Float, String>();
	ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);
	Identifier identifier = new Identifier(searcher);

	// Get list: similarity [0-1],image name.
	String[] similarImages = identifier.findSimilarImages(fileURI, indexesDir, maximumHits);
	
	// parse list and add each result to map.
	for (String similarImage : similarImages)
	{
	    System.out.println(similarImage);
	    //if the user request more hits than matches actually found, 
	    //matches array will be buffered with null.
	    //e.g. if user requested max 5 hits and 2 found
	    //results will be {hit1, hit2, null,null,null};
	    
	    if(similarImage == null)
		continue;
	    
	    // split similarity and image uri.
	    String[] tokens = similarImage.split(SIMILAR_IMAGE_NAME_SIMILARITY_SEPERATOR);

	    // check both elements are present
	    if (tokens.length >= 2)
	    {
		try
		{
		    // the first element is the similarity (float type)
		    float similarity = Float.parseFloat(tokens[0]);
		    // second value is uri.
		    String fileUri = tokens[1];
		    // get file name from uri.
		    int indexFileSeperator = fileUri.lastIndexOf(File.separator);
		    
		    String fileName = fileUri.substring(indexFileSeperator + 1);
		    similarityImageMap.put(similarity, fileName);
		} catch (NumberFormatException e)
		{
		    throw new LireResultsParseException("Lire Results did not match format %g:%s" + similarImage);
		}
	    } else
		throw new LireResultsParseException("Failure to parse results: " + similarImage);
	}

	return similarityImageMap;
    }

    private String getFoodNameForImage(String string) throws DataAccessObjectException
    {
	String foodName = null;

	if (connection != null)
	{
	    // TODO REFACTOR THIS LATER!!! I don't believe this is a very
	    // efficient approach
	    ImageDataAccessObject imageDao = new ImageDataAccessObject(connection);
	    FoodDataAccessObject foodDao = new FoodDataAccessObject(connection);

	    ImageDataTransferObject imageDTO = imageDao.find(string);
	    if (imageDTO != null)
	    {
		FoodDataTransferObject foodDTO = foodDao.find(imageDTO.getFoodId());
		foodName = foodDTO.getName();
	    }

	} else
	    throw new IllegalStateException("Null Connection");

	return foodName;
    }

    enum Status
    {
	TOO_FEW_ARGS(
		"Insufficient parameters provided.Service: identify/{required: file_name.jpg}/{optional: max_hits}"),
	INVALID_MAX_HITS("Invalid value provided for maximum number of hits. Value must be an integer."),
	IO_ERROR("An error occured while trying to identify the image."),
	FILE_NOT_FOUND("Search image not found on server. The file may have failed to upload or may have been deleted."),
	DB_ACCESS_ERROR("Failure to load food name from database."),
	LIRE_PARSE_ERROR("Failure to parse food identification results"),
	IDENTIFICATION_FAILED("Uploaded image could not be identified.");

	private final String message;

	private Status(String message)
	{
	    this.message = message;
	}

	public String getMessage()
	{
	    return message;
	}
    }
}
