package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.wks.calorieapp.models.Identifier;
import com.wks.calorieapp.models.Response;
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

    private static final int MIN_NUM_PARAMETERS = 2;
    private static final int DEFAULT_MAX_HITS = 10;
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

	String responseJSON = getSimilarImages(fileURI, maximumHits);
	out.println(responseJSON);
    }

    @SuppressWarnings("unchecked")
    private String getSimilarImages(String fileURI, int maximumHits)
    {
	// find similar images
	ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);
	Identifier identifier = new Identifier(searcher);

	try
	{
	    String[] similarImages = identifier.findSimilarImages(fileURI, indexesDir, maximumHits);
	    JSONArray similarImagesJSON = new JSONArray();
	    for (String uri : similarImages)
		similarImagesJSON.add(uri);

	    return new Response(true, similarImagesJSON.toJSONString()).toJSON();

	} catch (IOException e)
	{
	    logger.error("Identify Request. IOException encountered while returning similar images.", e);
	    return new Response(false, Status.IO_ERROR.getMessage()).toJSON();

	}
    }


    enum Status
    {
	TOO_FEW_ARGS(
		"Insufficient parameters provided.Service: identify/{required: file_name.jpg}/{optional: max_hits}"),
	INVALID_MAX_HITS("Invalid value provided for maximum number of hits. Value must be an integer."),
	IO_ERROR("An error occurred while reading the image file."),
	FILE_NOT_FOUND("Search image not found on server. The file may have failed to upload or may have been deleted."),
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
