package com.wks.calorieapp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.wks.calorieapp.models.FoodItem;
import com.wks.calorieapp.utils.DatabaseUtil;

public class FoodDataAccessObject
{

    private static final String TABLE_FOODS = "foods";
    private static final String COLUMN_FOOD_ID = "food_id";
    private static final String COLUMN_FOOD_NAME = "name";

    private static final String CREATE_QUERY = "INSERT INTO " + TABLE_FOODS + " ( " + COLUMN_FOOD_NAME + ") VALUES (?)";
    private static final String READ_QUERY = "SELECT " + COLUMN_FOOD_ID + ", " + COLUMN_FOOD_NAME + " FROM "
	    + TABLE_FOODS;
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_FOODS + " SET " + COLUMN_FOOD_NAME + "=? WHERE "
	    + COLUMN_FOOD_ID + " = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE_FOODS + " WHERE " + COLUMN_FOOD_ID + " = ?";
    private static final String FIND_QUERY = "SELECT " + COLUMN_FOOD_ID + ", " + COLUMN_FOOD_NAME + " FROM "
	    + TABLE_FOODS + " WHERE " + COLUMN_FOOD_ID + " = ?";

    private Connection connection = null;

    public FoodDataAccessObject(Connection connection) throws IllegalStateException
    {

	if (connection == null) throw new IllegalStateException("Null Connection");
	
	this.connection = connection;
    }

    public long create(FoodItem item) throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;

	try
	{
	    statement = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
	    statement.setString(1, item.getName());
	    statement.execute();

	    result = statement.getGeneratedKeys();
	    if (result != null && result.next())
	    {
		return result.getLong(1);
	    } else
		return -1;
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtil.close(statement,result);
	}

    }

    public List<FoodItem> read() throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	List<FoodItem> foodItems = new ArrayList<FoodItem>();
	FoodItem foodItem = null;

	try
	{
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(READ_QUERY);
	    statement.execute();

	    results = statement.getResultSet();
	    while (results != null && results.next())
	    {
		foodItem = new FoodItem();
		foodItem.setFoodId(results.getLong(COLUMN_FOOD_ID));
		foodItem.setName(results.getString(COLUMN_FOOD_NAME));
		foodItems.add(foodItem);
	    }
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtil.close(statement,results);
	}

	return foodItems;

    }

    public boolean update(FoodItem item) throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;

	try
	{
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(UPDATE_QUERY);
	    statement.setString(1, item.getName());
	    statement.setLong(2, item.getFoodId());
	    statement.execute();
	    return true;
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtil.close(statement);
	}

    }

    public boolean delete(long id) throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;

	try
	{
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(DELETE_QUERY);
	    statement.setLong(1, id);
	    statement.execute();
	    return true;
	} catch (SQLException e)
	{
	   throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtil.close(statement);
	}

    }

    public FoodItem find(long id) throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;
	FoodItem foodItem = null;

	try
	{

	    statement = connection.prepareStatement(FIND_QUERY);
	    statement.setLong(1, id);
	    statement.execute();

	    result = statement.getResultSet();
	    if (result != null && result.next())
	    {
		foodItem = new FoodItem();
		foodItem.setFoodId(result.getLong(COLUMN_FOOD_ID));
		foodItem.setName(result.getString(COLUMN_FOOD_NAME));
	    }
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtil.close(statement,result);
	}

	return foodItem;

    }

    public void close() 
    {
	DatabaseUtil.close(connection);
    }

}
