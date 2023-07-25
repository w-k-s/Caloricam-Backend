package com.wks.calorieapp.resources;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

import com.wks.calorieapp.utils.Environment;

public class LogInit extends HttpServlet
{

    private static final long serialVersionUID = 7103434869883214321L;

    private static final String PARAM_LOCAL_LOG4J = "local_log4j_properties";
    private static final String PARAM_WEB_LOG4J = "web_log4j_properties";
    

    @Override
    public void init() throws ServletException
    {
	Boolean appIsDeployed = Boolean.parseBoolean( getServletContext().getInitParameter("is_deployed") );
	String paramLog4j = appIsDeployed ? PARAM_WEB_LOG4J : PARAM_LOCAL_LOG4J;
	
	// make sure the log file exists.
	String logDir = Environment.getLogDirectory(getServletContext());
	File logDirectory = new File(logDir);
	if (!logDirectory.exists())
	    if (!logDirectory.mkdir()) return;

	String logUri = Environment.getLogFile(getServletContext());

	File logFile = new File(logUri);
	try
	{
	    if (!logFile.exists())
	    {
		if (!logFile.createNewFile()) getServletContext().setAttribute("log", "Logfile coult not be created");
	    } 
	} catch (IOException e)
	{
	    e.printStackTrace();
	}


	// load log4j properties file.
	String propertiesUriParameter = getServletConfig().getInitParameter(paramLog4j);
	String propertiesUri = System.getenv("$OPENSHIFT_REPO_DIR") + propertiesUriParameter;
	File propertiesFile = new File(propertiesUri);
	if (propertiesFile.exists())
	{
	    PropertyConfigurator.configure(propertiesUri);
	    getServletContext().setAttribute("properties", "properties exists " + propertiesUri);
	} else
	{
	    Properties log4jProperties = new Properties();
	    log4jProperties.setProperty("log4j.rootLogger", "INFO, file,stdout");
	    log4jProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
	    log4jProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
	    log4jProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
		    "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
	    log4jProperties.setProperty("log4j.appender.file", "org.apache.log4j.FileAppender");
	    log4jProperties.setProperty("log4j.appender.file.File", logUri);
	    log4jProperties.setProperty("log4j.appender.file.MaxFileSize","10MB");
	    log4jProperties.setProperty("log4j.appender.file.Append", "true");
	    log4jProperties.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
	    log4jProperties.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
	    PropertyConfigurator.configure(log4jProperties);
	   
	}

    }

}
