package com.wks.CalorieApp.Listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MySqlServletContextListener implements ServletContextListener {

    private static final String PARAM_DRIVER = "driver";
    private static final String PARAM_URL = "url";
    private static final String PARAM_USERNAME = "user";
    private static final String PARAM_PASSWORD = "password";
    
    private static final String ATTR_CONNECTION = "connection";

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
	// Connect
	String driver = event.getServletContext().getInitParameter(PARAM_DRIVER);
	String url = event.getServletContext().getInitParameter(PARAM_URL);
	String username = event.getServletContext().getInitParameter(PARAM_USERNAME);
	String password = event.getServletContext()
		.getInitParameter(PARAM_PASSWORD);

	try {
	    Class.forName(driver);
	    Connection connection = DriverManager.getConnection(url, username,
		    password);

	    event.getServletContext().setAttribute(ATTR_CONNECTION, connection);

	    
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

}
