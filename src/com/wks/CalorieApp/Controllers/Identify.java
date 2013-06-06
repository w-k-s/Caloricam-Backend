package com.wks.CalorieApp.Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.json.simple.JSONArray;

import com.wks.CalorieApp.Models.Identifier;
import com.wks.CalorieApp.Utils.Environment;
import com.wks.CalorieApp.Utils.JSONHelper;

/*
 * - think of a better JSON writer impmentation.
 * - create an abstract class for index writing
 * - handle duplication??
 */

public class Identify extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String PARAMETER_SEPERATOR = "/";

    private static final int MIN_NUM_PARAMETERS = 2;
    private static final int DEFAULT_MAX_HITS = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	doPost(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();
	
	String imagesDir = Environment.getImagesDirectory(getServletContext());
	String indexesDir = Environment.getIndexesDirectory(getServletContext());
	int defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(Parameter.DEFAULT_MAX_HITS.toString())) | DEFAULT_MAX_HITS;
	int maximumHits = defaultMaxHits;

	// check that parameters were provided
	if (req.getPathInfo() == null)
	{
	    // TODO
	   out.println( JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));
	    return;
	}

	// seperate parameters
	String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

	if (parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println( JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));
	    return;
	}

	// validate parameters
	if (parameters.length > MIN_NUM_PARAMETERS)
	{
	    try
	    {
		maximumHits = Integer.parseInt(parameters[2]);
	    } catch (Exception e)
	    {
		out.println( JSONHelper.writeStatus(false, Status.INVALID_MAX_HITS.getMessage()));
		return;
	    }
	}

	// check that file exists
	String fileURI = imagesDir + parameters[1];

	// find similar images
	ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);
	Identifier identifier = new Identifier(searcher);

	try
	{
	    String[] similarImages = identifier.findSimilarImages(fileURI, indexesDir, maximumHits);
	    JSONArray similarImagesJSON = new JSONArray();
	    for (String uri : similarImages)
		similarImagesJSON.add(uri);

	    out.println( JSONHelper.writeStatus(true, similarImagesJSON.toJSONString()));
	    
	} catch (IOException ioe)
	{
	    // TODO create indexer codes
	    out.println( JSONHelper.writeStatus(false, Status.IO_ERROR.getMessage()));
	    ioe.printStackTrace();
	}catch(Exception e)
	{
	   out.println( JSONHelper.writeStatus(false, Status.IDENTIFICATION_FAILED.getMessage()));
	   e.printStackTrace();
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

	public String getMessage() {
	    return message;
	}
    }
}
