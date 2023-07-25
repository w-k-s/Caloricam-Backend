package com.wks.calorieapp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.wks.calorieapp.entities.User;
import com.wks.calorieapp.utils.DatabaseUtils;

public class UserDAO
{

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_QUERY = "INSERT INTO " + TABLE_USERS + " (" + COLUMN_USERNAME + ", "
	    + COLUMN_PASSWORD + ") VALUES (?,?)";
    private static final String READ_QUERY = "SELECT " + COLUMN_USERNAME + ", " + COLUMN_PASSWORD + " FROM "
	    + TABLE_USERS;
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_USERS + " SET " + COLUMN_PASSWORD + "= ? WHERE "
	    + COLUMN_USERNAME + " = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
    private static final String FIND_QUERY = "SELECT " + COLUMN_USERNAME + ", " + COLUMN_PASSWORD + " FROM "
	    + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";

    private Connection connection = null;

    public UserDAO(Connection connection)
    {
	if (connection == null)
	    throw new IllegalStateException("Null Connection");
	this.connection = connection;
    }

    public boolean create(User user) throws DataAccessObjectException
    {
	// Connection connection = null;
	PreparedStatement statement = null;
	boolean success = false;

	try
	{
	    // connection = DatabaseUtils.getConnection();
	    statement = connection.prepareStatement(CREATE_QUERY);
	    statement.setString(1, user.getUsername());
	    statement.setString(2, user.getPassword());
	    statement.executeUpdate();
	    success = true;
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtils.close(statement);
	}
	return success;
    }

    public Map<String, User> read() throws DataAccessObjectException
    {
	Map<String, User> usersList = new HashMap<String, User>();
	User user = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	try
	{
	    statement = connection.prepareStatement(READ_QUERY);
	    statement.execute();
	    results = statement.getResultSet();

	    while (results.next())
	    {
		String username = results.getString(COLUMN_USERNAME);
		String password = results.getString(COLUMN_PASSWORD);

		user = new User(username, password);

		usersList.put(username, user);
	    }

	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtils.close(statement, results);
	}

	return usersList;
    }

    public boolean update(User user) throws DataAccessObjectException
    {

	PreparedStatement statement = null;

	try
	{

	    statement = connection.prepareStatement(UPDATE_QUERY);
	    statement.setString(1, user.getPassword());
	    statement.setString(2, user.getUsername());
	    statement.execute();
	    return true;
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException (e);
	} finally
	{
	    DatabaseUtils.close(statement);
	}

    }

    public boolean delete(User user) throws DataAccessObjectException
    {
	PreparedStatement statement = null;

	try
	{
	    statement = connection.prepareStatement(DELETE_QUERY);
	    statement.setString(1, user.getUsername());
	    statement.execute();
	    return true;
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtils.close(statement);
	}

    }

    public User find(String id) throws DataAccessObjectException
    {

	PreparedStatement statement = null;
	ResultSet result = null;
	User user = null;

	try
	{
	    statement = connection.prepareStatement(FIND_QUERY);
	    statement.setString(1, id);
	    statement.execute();

	    result = statement.getResultSet();
	    if (result.next() && result != null)
	    {

		String username = result.getString(COLUMN_USERNAME);
		String password = result.getString(COLUMN_PASSWORD);
		user = new User(username, password);
	    }

	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	} finally
	{
	    DatabaseUtils.close(statement, result);
	}
	return user;
    }

    public void close()
    {
	DatabaseUtils.close(connection);
    }


}
