package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDataAccessObject;
import com.wks.calorieapp.daos.ImageDataAccessObject;
import com.wks.calorieapp.models.FoodDataTransferObject;
import com.wks.calorieapp.models.ImageDataTransferObject;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.models.StatusCode;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class Update extends HttpServlet
{
    private static final Logger logger = Logger.getLogger(Update.class);

    private static final String CONTENT_TYPE = "application/json";
    private static final int MIN_NUM_PARAMETERS = 3;

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

	String imageName = "";
	String foodName = "";

	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters != null && parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());

	} else
	{
	    imageName = parameters[1];
	    foodName = parameters[2];

	    File imageFile = new File(imagesDir + imageName);
	    if (imageFile.exists())
	    {
		try
		{
		    long foodId = getFoodId(foodName);
		    String imageId = getImageId(imageFile);
		    if (foodId != -1 && imageId != null)
		    {
			if (linkImageWithFood(imageName, foodId))
			{
			    out.println(new Response(StatusCode.OK).toJSON());
			} else
			{
			    String details = "image: " + imageName + ", foodName: " + foodName;
			    out.println(new Response(StatusCode.UPDATE_FAILED, StatusCode.UPDATE_FAILED
				    .getDescription(details)).toJSON());
			}
		    } else
		    {
			throw new DataAccessObjectException(foodName + " could not be inserted into db. FoodId = "
				+ foodId + ", imageId=" + imageName);
		    }
		} catch (DataAccessObjectException e)
		{
		    out.println(new Response(StatusCode.DB_INSERT_FAILED).toJSON());
		    logger.error("Update request Failed. Food Item " + foodName + " could not be inserted into db.", e);
		}
	    } else
	    {
		out.println(new Response(StatusCode.FILE_NOT_FOUND, StatusCode.FILE_NOT_FOUND.getDescription(imageName))
			.toJSON());
		logger.info("Update request failed. " + imageFile.getAbsolutePath() + " does not exist.");
	    }
	}
    }

    private long getFoodId(String foodName) throws DataAccessObjectException
    {
	long foodId = -1;
	if (connection != null)
	{
	    FoodDataAccessObject foodDao = new FoodDataAccessObject(connection);
	    FoodDataTransferObject foodDto = foodDao.find(foodName);
	    if (foodDto != null)
	    {
		foodId = foodDto.getFoodId();
	    } else
	    {
		foodDto = new FoodDataTransferObject();
		foodDto.setName(foodName);
		foodId = foodDao.create(foodDto);
	    }
	}
	return foodId;
    }

    // This code will insert the image into the db if it hasnt already been
    // inserted.
    private String getImageId(File imageFile) throws DataAccessObjectException
    {
	String imageId = null;
	if (connection != null)
	{
	    ImageDataAccessObject imageDao = new ImageDataAccessObject(connection);
	    ImageDataTransferObject imageDto = imageDao.find(imageFile.getName());
	    if (imageDto != null)
	    {
		imageId = imageDto.getImageId();
	    } else
	    {
		imageDto = new ImageDataTransferObject();
		imageDto.setImageId(imageFile.getName());
		imageDto.setSize(imageFile.length());
		imageDto.setFinalized(false);
		imageId = imageFile.getName();
	    }
	}
	return imageId;
    }

    private boolean linkImageWithFood(String imageId, long foodId) throws DataAccessObjectException
    {
	boolean success = false;
	ImageDataAccessObject imageDao = new ImageDataAccessObject(connection);
	ImageDataTransferObject imageDto = imageDao.find(imageId);
	if (imageDto != null)
	{
	    imageDto.setFoodId(foodId);
	    success = imageDao.update(imageDto);
	}
	return success;
    }
}
