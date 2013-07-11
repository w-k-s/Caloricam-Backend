package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.entities.StatusCode;
import com.wks.calorieapp.services.Identifier;
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
    private static Logger logger = Logger.getLogger(Identify.class);

    @Override
    public void init() throws ServletException
    {
	imagesDir = Environment.getImagesDirectory(getServletContext());
	indexesDir = Environment.getIndexesDirectory(getServletContext());
	defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(Parameter.DEFAULT_MAX_HITS.toString()));
	defaultMinSimilarity = Float.parseFloat(getServletContext().getInitParameter(
		Parameter.DEFAULT_MIN_SIMILARITY.toString()));
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
	    logger.error("Identify Request. Invalid number of maximum Hits provided: " + parameters[2], e);
	    return;
	}

	// check that file exists
	String fileURI = imagesDir + fileName;

	logger.info("Identify Request. Image: " + fileURI + " maximumHits: " + maximumHits);

	try
	{
	    Identifier identifier = Identifier.getInstance(connection);
	    Map<String,Float> foodNameSimilarity = identifier.getPossibleFoodsForImage(fileURI, indexesDir, minimumSimilarity, maximumHits);
	    
	    String jsonMap = JSONValue.toJSONString(foodNameSimilarity);

	    out.println(new Response(StatusCode.OK, jsonMap).toJSON());

	} catch (DataAccessObjectException e)
	{

	    logger.error("Failure to load food name from database", e);
	    out.println(new Response(StatusCode.DB_SQL_EXCEPTION).toJSON());

	} catch (IOException e)
	{
	    logger.error("IO Exception encountered while finding similar image.", e);
	    out.println(new Response(StatusCode.FILE_IO_ERROR).toJSON());
	}

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
