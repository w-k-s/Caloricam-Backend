package com.wks.CalorieApp.DataAccessObjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.wks.CalorieApp.Models.FoodItem;

public class FoodDataAccessObject {

    private static final String TABLE_FOODS = "foods";
    private static final String COLUMN_FOOD_ID = "food_id";
    private static final String COLUMN_FOOD_NAME = "name";

    private static final String CREATE_QUERY = "INSERT INTO " + TABLE_FOODS
	    + " ( " + COLUMN_FOOD_NAME + ") VALUES (?)";
    private static final String READ_QUERY = "SELECT " + COLUMN_FOOD_ID + ", "
	    + COLUMN_FOOD_NAME + " FROM " + TABLE_FOODS;
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_FOODS
	    + " SET " + COLUMN_FOOD_NAME + "=? WHERE " + COLUMN_FOOD_ID
	    + " = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE_FOODS
	    + " WHERE " + COLUMN_FOOD_ID + " = ?";
    private static final String FIND_QUERY = "SELECT " + COLUMN_FOOD_ID + ", "
	    + COLUMN_FOOD_NAME + " FROM " + TABLE_FOODS + " WHERE "
	    + COLUMN_FOOD_ID + " = ?";

    private static final String ATTR_CONNECTION = "connection";

    private Connection connection = null;

    public FoodDataAccessObject(ServletContext context)
	    throws IllegalStateException {
	connection = (Connection) context.getAttribute(ATTR_CONNECTION);
	if (connection == null)
	    throw new IllegalStateException("Null Connection");
    }

    public long create(FoodItem item) {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;

	try {
	    statement = connection.prepareStatement(CREATE_QUERY,
		    Statement.RETURN_GENERATED_KEYS);
	    statement.setString(1, item.getName());
	    statement.execute();

	    result = statement.getGeneratedKeys();
	    if (result != null && result.next()) {
		return result.getLong(1);
	    } else
		return -1;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		result.close();
		statement.close();
		// connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return -1;
    }

    public List<FoodItem> read() {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	List<FoodItem> foodItems = new ArrayList<FoodItem>();
	FoodItem foodItem = null;

	try {
	   // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(READ_QUERY);
	    statement.execute();

	    results = statement.getResultSet();
	    while (results != null && results.next()) {
		foodItem = new FoodItem();
		foodItem.setFoodId(results.getLong(COLUMN_FOOD_ID));
		foodItem.setName(results.getString(COLUMN_FOOD_NAME));
		foodItems.add(foodItem);
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		results.close();
		statement.close();
		// connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return foodItems;

    }

    public boolean update(FoodItem item) {
	// Connection connection = null;
	PreparedStatement statement = null;

	try {
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(UPDATE_QUERY);
	    statement.setString(1, item.getName());
	    statement.setLong(2, item.getFoodId());
	    statement.execute();
	    return true;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		// connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return false;
    }

    public boolean delete(long id) {
	// Connection connection = null;
	PreparedStatement statement = null;

	try {
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(DELETE_QUERY);
	    statement.setLong(1, id);
	    statement.execute();
	    return true;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		// connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return false;
    }

    public FoodItem findFoodItemById(long id) {
	// Connection connection = null;
	PreparedStatement statement = null;
	ResultSet result = null;
	FoodItem foodItem = null;
	
	try {	    

	    statement = connection.prepareStatement(FIND_QUERY);
	    statement.setLong(1, id);
	    statement.execute();

	    result = statement.getResultSet();
	    if (result != null && result.next()) {
		foodItem = new FoodItem();
		foodItem.setFoodId(result.getLong(COLUMN_FOOD_ID));
		foodItem.setName(result.getString(COLUMN_FOOD_NAME));
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		result.close();
		statement.close();
		// connection.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return foodItem;

    }

    //NOTES: http://javarevisited.blogspot.ae/2012/03/finalize-method-in-java-tutorial.html
    public void close() throws SQLException
    {
	connection.close();
    }
    
    @Override
    protected void finalize() throws Throwable {
	try {
	    if(!connection.isClosed())
		connection.close();
	} catch (Exception e) {
	    throw e;
	} finally {
	    super.finalize();
	}
    }

}
