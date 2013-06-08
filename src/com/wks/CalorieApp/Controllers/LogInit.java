package com.wks.CalorieApp.Controllers;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.wks.CalorieApp.Utils.Environment;

public class LogInit extends HttpServlet
{
    @Override
    public void init() throws ServletException
    {
	//make sure the log file exists.
	String logDir = Environment.getLogDirectory(getServletContext());
	    File logDirectory = new File(logDir);
	    if(!logDirectory.exists())
		logDirectory.mkdir();
	
	//load log4j properties file.
	String propertiesUriParameter = getServletConfig().getInitParameter("log4j_properties_uri");

	String root = Environment.getRootDirectory(getServletContext());
	String propertiesUri = root + propertiesUriParameter;
	File propertiesFile = new File(propertiesUri);
	if (propertiesFile.exists())
	{
	    PropertyConfigurator.configure(propertiesUri);
	} else
	{
	    
	    Properties log4jProperties = new Properties();
	    log4jProperties.setProperty("log4j.rootLogger", "ERROR, file,stdout");
	    log4jProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
	    log4jProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
	    log4jProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern","%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
	    log4jProperties.setProperty("log4j.appender.file", "org.apache.log4j.ConsoleAppender");
	    log4jProperties.setProperty("log4j.appender.file.File", logDir+"log.txt");
	    log4jProperties.setProperty("log4j.appender.file.Append","true");
	    log4jProperties.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
	    log4jProperties.setProperty("log4j.appender.file.layout.ConversionPattern",
		    "%-5p %c %x - %m%n");
	    PropertyConfigurator.configure(log4jProperties);
	}

    }
}
