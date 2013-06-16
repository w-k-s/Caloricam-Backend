package com.wks.calorieapp.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneralDataAccessObject
{
    private Connection connection;

    public GeneralDataAccessObject(Connection connection)
    {
	if (connection == null) throw new IllegalStateException("Null Connection");

	this.connection = connection;
    }

    public boolean doQuery(String query) throws DataAccessObjectException
    {
	Statement statement = null;

	try
	{
	    statement = connection.createStatement();
	    return statement.execute(query);
	} catch (SQLException e)
	{
	    throw new DataAccessObjectException(e);
	}

    }
}
