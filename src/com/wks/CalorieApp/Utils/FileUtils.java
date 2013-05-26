package com.wks.CalorieApp.Utils;

import java.io.File;

import javax.servlet.ServletContext;

public class FileUtils {

	private final static String PARAM_RESOURCES_DIR = "resources_dir";
	private final static String PARAM_IMAGES_DIR = "images_dir";
	private final static String PARAM_INDEXES_DIR = "indexes_dir";
	
	public static String getResourcesDirectory(ServletContext context)
	{
		
		// get name of images directory
		String resourcesDir = context.getInitParameter(PARAM_RESOURCES_DIR);
		
		// get path to context root directory
		String contextRoot = context.getRealPath("/");

		// remove trailing '/'
		contextRoot = contextRoot.substring(0, contextRoot.length() - 1);

		// get path of directory outside root, where images will be saved.
		String outsideRoot = contextRoot.substring(0,
				contextRoot.lastIndexOf("/"));
		
		
		/*
		 * Uncomment the string below for deployment.
		 */
		//String outsideRoot = "$OPENSHIFT_DATA_DIR";
		
		return outsideRoot + File.separator + resourcesDir;
	}
	
	public static String getImagesDirectory(ServletContext context) {

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

}
