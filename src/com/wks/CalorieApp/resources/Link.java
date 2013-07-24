package com.wks.calorieapp.resources;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.services.Linker;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;

public class Link extends HttpServlet
{
 
    private static final long serialVersionUID = -8335463864848081067L;

    private static final Logger logger = Logger.getLogger(Link.class);

    private static final String CONTENT_TYPE = "application/json";
    
    private static final String PARAM_IMAGE_NAME = "image_name";
    private static final String PARAM_FOOD_NAME = "food_name";

    private static Connection connection = null;
    private static String imagesDir = "";

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtils.getConnection();
	imagesDir = Environment.getImagesDirectory(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	// TODO Auto-generated method stub
	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	String imageName = req.getParameter(PARAM_IMAGE_NAME);
	String foodName = URLDecoder.decode(req.getParameter(PARAM_FOOD_NAME),"UTF-8");


	if (imageName == null || foodName == null || imageName.isEmpty() || foodName.isEmpty())
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());
	    return;
	}


	File imageFile = new File(imagesDir + imageName);
	if (!imageFile.exists())
	{
	    out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
	    logger.info("Update request failed. " + imageFile.getAbsolutePath() + " does not exist.");
	    return;
	}
	
	try
	{
	    Linker linker = new Linker(connection);
	    boolean linked = linker.linkImageWithFood(foodName, imageFile);
	    StatusCode statusCode = linked? StatusCode.OK : StatusCode.LINK_FAILED;
	    Response response = new Response(statusCode);
	    out.println(response.toJSON());
	} catch (DataAccessObjectException e)
	{
	    out.println(new Response(StatusCode.DB_INSERT_FAILED).toJSON());
	    logger.error("Update request Failed. Food Item " + foodName + " could not be inserted into db.", e);
	}

    }

   
}
