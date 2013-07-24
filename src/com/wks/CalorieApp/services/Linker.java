package com.wks.calorieapp.services;

import java.io.File;
import java.sql.Connection;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDAO;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.entities.ImageEntry;

public class Linker
{
    private static Connection connection;

    public Linker(Connection connection)
    {
	Linker.connection = connection;
    }

    public boolean linkImageWithFood(String foodName, File imageFile) throws DataAccessObjectException
    {
	boolean success = false;
	String imageName = imageFile.getName();
	
	long foodId = getFoodId(foodName);
	String imageId = getImageId(imageFile);
	if (foodId != -1 && imageId != null)
	{
	    success = (linkImageWithFood(imageName, foodId));
	} 
	
	return success;
    }
    
    private long getFoodId(String foodName) throws DataAccessObjectException
    {
	long foodId = -1;
	if (connection != null)
	{
	    FoodDAO foodDao = new FoodDAO(connection);
	    FoodEntry foodDto = foodDao.read(foodName);
	    if (foodDto != null)
	    {
		foodId = foodDto.getFoodId();
	    } else
	    {
		foodDto = new FoodEntry();
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
	    imageDAO imageDao = new imageDAO(connection);
	    ImageEntry imageDto = imageDao.find(imageFile.getName());
	    if (imageDto != null)
	    {
		imageId = imageDto.getImageId();
	    } else
	    {
		imageDto = new ImageEntry();
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
	if (connection != null)
	{
	    imageDAO imageDao = new imageDAO(connection);
	    ImageEntry imageDto = imageDao.find(imageId);
	    if (imageDto != null)
	    {
		imageDto.setFoodId(foodId);
		success = imageDao.update(imageDto);
	    }
	}

	return success;
    }
}
