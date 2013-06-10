package com.wks.calorieapp.utils;

import java.io.File;

import javax.servlet.ServletContext;

//TODO Change resources dir to localhost
public class Environment
{
    private final static String PARAM_RESOURCES_DIR = "resources_dir";
    private final static String PARAM_IMAGES_DIR = "images_dir";
    private final static String PARAM_INDEXES_DIR = "indexes_dir";
    private final static String PARAM_LOG_DIR = "log_dir";
    private final static String PARAM_LOG_FILE = "log_file";
    

    public static String getRootDirectory(ServletContext context)
    {
	return context.getRealPath("/");
    }
    
    public static String getResourcesDirectory(ServletContext context)
    {
	boolean appIsDeployed = Boolean.parseBoolean(context.getInitParameter("is_deployed"));
	
	// get name of images directory
	String resourcesDir = context.getInitParameter(PARAM_RESOURCES_DIR);
	String outsideRoot = "";
	if (!appIsDeployed)
	{
	    // get path to context root directory
	    String contextRoot = context.getRealPath("/");

	    // remove trailing '/'
	    contextRoot = contextRoot.substring(0, contextRoot.length() - 1);

	    // get path of directory outside root, where images will be saved.
	    outsideRoot = contextRoot.substring(0, contextRoot.lastIndexOf("/"));

	} else
	{
	    outsideRoot = "/var/lib/openshift/519f8e8c4382ec1eb0000156/app-root/data";
	}
	return outsideRoot + File.separator + resourcesDir;
    }

    public static String getImagesDirectory(ServletContext context)
    {

	String imagesDir = context.getInitParameter(PARAM_IMAGES_DIR);
	String resourcesDir = getResourcesDirectory(context);
	return resourcesDir + imagesDir;
    }

    public static String getIndexesDirectory(ServletContext context)
    {
	String indexesDir = context.getInitParameter(PARAM_INDEXES_DIR);
	String resourcesDir = getResourcesDirectory(context);
	return resourcesDir + indexesDir;
    }

    
    public static String getLogDirectory(ServletContext context)
    {

	String logDir = context.getInitParameter(PARAM_LOG_DIR);
	String resourcesDir = getResourcesDirectory(context);
	return resourcesDir + logDir;
    }
    
    public static String getLogFile(ServletContext context)
    {
	String logFile = context.getInitParameter(PARAM_LOG_FILE);
	String logDir = getLogDirectory(context);
	return logDir + logFile;
    }

}
