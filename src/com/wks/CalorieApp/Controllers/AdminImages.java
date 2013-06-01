package com.wks.CalorieApp.Controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.wks.CalorieApp.Utils.*;

public class AdminImages extends HttpServlet {

    private static final String ATTR_IMAGE_LIST = "images";
    private static final String ATTR_IMAGE_DIR = "image_dir";
    private static final String ATTR_AUTHENTICATED = "authenticated";
    private static final String[] EXTENSIONS = { ".jpeg", ".jpg" };

    protected void doGet(javax.servlet.http.HttpServletRequest req,
	    javax.servlet.http.HttpServletResponse resp)
	    throws javax.servlet.ServletException, java.io.IOException {

	// laods all images, stores them into a list and passes it as an
	// attribute to
	// images.jsp
	boolean authenticated = false;

	HttpSession session = req.getSession();
	synchronized (session) {
	    Boolean b = (Boolean) session.getAttribute(ATTR_AUTHENTICATED);
	    if (b != null)
		authenticated = b;
	}

	if (!authenticated) {
	    RequestDispatcher login = req.getRequestDispatcher("/login");
	    login.forward(req, resp);
	    return;
	}

	List<String> files = FileUtils.getFilesInDir(
		Environment.getImagesDirectory(getServletContext()),EXTENSIONS);
	
	req.setAttribute(ATTR_IMAGE_LIST, files);
	req.setAttribute(ATTR_IMAGE_DIR, Environment.getImagesDirectory(getServletContext()));
	RequestDispatcher request = req.getRequestDispatcher("/WEB-INF/images.jsp");
	request.forward(req, resp);
    };

}
