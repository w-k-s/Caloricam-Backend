package com.wks.calorieapp.services;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDAO;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.entities.ImageEntry;

public class Linker
{
    private static Connection connection;
    private static Logger logger = Logger.getLogger(Linker.class);

    public Linker(Connection connection)
    {
	Linker.connection = connection;
    }

    /**Links food in image with given food name.
     * 
     * @param foodName
     * @param imageFile
     * @return true if linking was succesful.
     * @throws DataAccessObjectException
     */
    public boolean linkImageWithFood(String foodName, File imageFile) throws DataAccessObjectException
    {
	boolean success = false;
	String imageName = imageFile.getName();
	
	//get id of food in food database
	//getFoodId will create record if it doesnt already exist.
	//likewise for imageId.
	long foodId = getFoodId(foodName);
	String imageId = getImageId(imageFile);
	if (foodId != -1 && imageId != null)
	{
	    //set image.foodId = food.id
	    success = (linkImageWithFood(imageName, foodId));
	    logger.info("Linker. ImageName: "+imageName+", FoodId: "+foodId+", Success: "+success);
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
