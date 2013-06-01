package com.wks.CalorieApp.Controllers;

import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.wks.CalorieApp.Utils.*;

public class AdminImages extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_IMAGE = "img";
    
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_VIEW = "view";
    
    private static final String ATTR_IMAGE_LIST = "images";
    private static final String ATTR_IMAGE_DIR = "image_dir";
    private static final String ATTR_AUTHENTICATED = "authenticated";
    
    private static final String SRVLT_LOGIN = "/login";
    
    private static final String[] EXTENSIONS = { ".jpeg", ".jpg" };

    protected void doGet(javax.servlet.http.HttpServletRequest req,
	    javax.servlet.http.HttpServletResponse resp)
	    throws javax.servlet.ServletException, java.io.IOException {

	// laods all images, stores them into a list and passes it as an
	// attribute to
	// images.jsp
	boolean authenticated = false;
	String action = null;
	String image = null;

	HttpSession session = req.getSession();
	synchronized (session) {
	    Boolean b = (Boolean) session.getAttribute(ATTR_AUTHENTICATED);
	    if (b != null)
		authenticated = b;
	}
	
	action = req.getParameter(PARAM_ACTION);
	image = req.getParameter(PARAM_IMAGE);

	if (!authenticated) {
	    RequestDispatcher login = req.getRequestDispatcher(SRVLT_LOGIN);
	    login.forward(req, resp);
	    return;
	}
	
	if(action != null && image != null)
	    handleAction(action,image);
	

	List<String> files = FileUtils.getFilesInDir(
		Environment.getImagesDirectory(getServletContext()),EXTENSIONS);
	
	req.setAttribute(ATTR_IMAGE_LIST, files);
	req.setAttribute(ATTR_IMAGE_DIR, Environment.getImagesDirectory(getServletContext()));
	RequestDispatcher request = req.getRequestDispatcher("/WEB-INF/images.jsp");
	request.forward(req, resp);
    }

    private void handleAction(String action, String image)
    {
	if(action.equalsIgnoreCase(ACTION_DELETE))
	{
	    String fileURI = Environment.getImagesDirectory(getServletContext())+image;
	    if(!deleteFile(fileURI))
		System.out.println("Deleting file failes");
	    
	    return;
	}
	if(action.equalsIgnoreCase(ACTION_VIEW))
	{
	    
	}
    };
    
    private boolean deleteFile(String file)
    {
	return FileUtils.deleteFile(file);
    }

}
