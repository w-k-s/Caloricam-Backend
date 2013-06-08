package com.wks.CalorieApp.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil
{
    private static final boolean appIsDeployed = false;
    private static final String PARAM_DRIVER = "driver";
    private static final String PARAM_URL = "url";
    private static final String PARAM_USERNAME = "user";
    private static final String PARAM_PASSWORD = "password";
    private static final String DB_PROPERTIES_FILE = appIsDeployed ? "web_db.properties" : "local_db.properties";

    private static Connection connection = null;

    public static Connection getConnection()
    {
	if (connection != null) return connection;

	try
	{
	    Properties dbProperties = new Properties();
	    InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE);
	    dbProperties.load(input);
	    String url = "";
	    if (appIsDeployed)
	    {

		url = "jdbc:mysql://127.12.26.130:3306/uploadte";
	    } else
	    {
		url = dbProperties.getProperty(PARAM_URL);
	    }
	    String driver = dbProperties.getProperty(PARAM_DRIVER);
	    String username = dbProperties.getProperty(PARAM_USERNAME);
	    String password = dbProperties.getProperty(PARAM_PASSWORD);

	    Class.forName(driver);
	    connection = DriverManager.getConnection(url, username, password);
	} catch (ClassNotFoundException e)
	{
	    e.printStackTrace();
	} catch (SQLException e)
	{
	    e.printStackTrace();
	} catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
	return connection;
    }
}
