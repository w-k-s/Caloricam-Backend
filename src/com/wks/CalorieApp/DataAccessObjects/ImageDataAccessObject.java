package com.wks.CalorieApp.DataAccessObjects;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wks.CalorieApp.Models.ImageItem;

public class ImageDataAccessObject {
    
    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_FOOD_ID = "food_id";
    private static final String COLUMN_SIZE = "size";
    private static final String COLUMN_IS_FINALIZED = "is_finalized";
    
    private static final String CREATE_QUERY = "INSERT INTO "+TABLE_IMAGES+" ("+COLUMN_IMAGE_ID+", "+COLUMN_FOOD_ID+","+COLUMN_SIZE+","+COLUMN_IS_FINALIZED+") VALUES (?,?,?,?)";
    private static final String READ_QUERY = "SELECT "+COLUMN_IMAGE_ID+", "+COLUMN_FOOD_ID+", "+COLUMN_SIZE+","+COLUMN_IS_FINALIZED+" FROM "+TABLE_IMAGES;
    private static final String UPDATE_QUERY = "UPDATE "+TABLE_IMAGES+" SET "+COLUMN_FOOD_ID+"=? , "+COLUMN_SIZE+"=?,"+COLUMN_IS_FINALIZED+"=? WHERE "+COLUMN_IMAGE_ID+" = ?";
    private static final String DELETE_QUERY = "DELETE FROM images WHERE image_id = ?";
    private static final String FIND_QUERY = "SELECT "+COLUMN_IMAGE_ID+", "+COLUMN_FOOD_ID+", "+COLUMN_SIZE+","+COLUMN_IS_FINALIZED+" FROM "+TABLE_IMAGES + " WHERE "+COLUMN_IMAGE_ID+" = ?";
    
    public void create(ImageItem image)
    {
	Connection connection = null;
	PreparedStatement statement = null;

	try {
	    //connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(CREATE_QUERY);
	    statement.setString(1, image.getImageId());
	    statement.setLong(2, image.getFoodId());
	    statement.setLong(3, image.getSize());
	    statement.setBoolean(4, image.isFinalized());
	    statement.executeUpdate();
	       
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}finally{
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	 
    }
    
    public List<ImageItem> read()
    {
	List<ImageItem> imageItems = new ArrayList<ImageItem>();
	ImageItem imageItem = null;
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	
	try {
	    //connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(READ_QUERY);
	    statement.execute();
	    results = statement.getResultSet();
	    
	    while(results.next())
	    {
	        imageItem = new ImageItem();
	        imageItem.setImageId(results.getString(COLUMN_IMAGE_ID));
	        imageItem.setFoodId(results.getLong(COLUMN_FOOD_ID));
	        imageItem.setSize(results.getLong(COLUMN_SIZE));
	        imageItem.setFinalized(results.getBoolean(COLUMN_IS_FINALIZED));
	        imageItems.add(imageItem);
	    }
	    
	    return imageItems;
	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}finally{
	    try {
		results.close();
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	return null;
	
	
    }
    
    public boolean update(ImageItem image)
    {
	Connection connection = null;
	PreparedStatement statement = null;
	
	try {
	    //connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(UPDATE_QUERY);
	    statement.setLong(1, image.getFoodId());
	    statement.setLong(2, image.getSize());
	    statement.setBoolean(3, image.isFinalized());
	    statement.setString(4, image.getImageId());
	    statement.execute();
	    return true;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}finally{
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return false;
    }
    
    public boolean delete(String id)
    {
	Connection connection = null;
	PreparedStatement statement = null;
	
	try {
	    //connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(DELETE_QUERY);
	    statement.setString(1, id);
	    statement.execute();
	    return true;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}finally{
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return false;
    }
    
    public ImageItem findImageById(String id)
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;
	ImageItem image = null;
	
	try {
	    //connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(FIND_QUERY);
	    statement.setString(1, id);
	    statement.execute();
	    
	    result = statement.getResultSet();
	    if(result.next() && result != null)
	    {
	        image = new ImageItem();
	        image.setImageId(result.getString(COLUMN_IMAGE_ID));
	        image.setFoodId(result.getLong(COLUMN_FOOD_ID));
	        image.setSize(result.getLong(COLUMN_SIZE));
	        image.setFinalized(result.getBoolean(COLUMN_IS_FINALIZED));
	        
	    }
	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}finally{
	    try {
		result.close();
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return image;
	
    }
}
