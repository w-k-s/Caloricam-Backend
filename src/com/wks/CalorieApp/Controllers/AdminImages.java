package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.utils.*;

public class AdminImages extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_VIEW = "view";

    private static final String JSP_IMAGE = "/WEB-INF/images.jsp";
    private static final String SRVLT_LOGIN = "/login";

    private static final String[] EXTENSIONS = { ".jpeg", ".jpg" };
    private static final String DEFAULT_MIME_TYPE = "image/jpeg";
    private static final String REDIRECT = "/calorieapp";
    private static Logger logger = Logger.getLogger(AdminImages.class);
    private static Connection connection = null;

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtils.getConnection();
    }

    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
	    throws javax.servlet.ServletException, java.io.IOException
    {

	// laods all images, stores them into a list and passes it as an
	// attribute to images.jsp

	boolean authenticated = false;
	String username = "";
	String action = null;
	String image = null;

	
	// session is not a thread-safe variable.
	HttpSession session = req.getSession();
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    username = (String) session.getAttribute(Attribute.USERNAME.toString());
	    if (b != null) authenticated = b;
	}

	action = req.getParameter(Parameter.ACTION.toString());
	image = req.getParameter(Parameter.IMAGE.toString());

	if (!authenticated)
	{
	    
	    // redirect to login page
	    logger.info("Admin Image. Page requested. User not authenticated");
	    resp.sendRedirect(REDIRECT + SRVLT_LOGIN);
	    return;
	    
	}else
	{
	    logger.info("Admin Image. Page requested by "+username);
	}

	logger.info("Admin. Request contained action='"+action+"', image='"+image+"'.");
	if (action != null && image != null)
	{
	    
	    try
	    {
		handleAction(action, image, resp);
		 if (action.equalsIgnoreCase(ACTION_VIEW)) return;
	    } catch (DataAccessObjectException e)
	    {
		logger.error("Admin. Database error encountered while deleting image: "+image,e);
		e.printStackTrace();
	    }
	   
	}

	List<String> imageURIList = FileUtils.getFilesInDir(Environment.getImagesDirectory(getServletContext()),
		EXTENSIONS);

	req.setAttribute(Attribute.IMAGE_LIST.toString(), imageURIList);
	RequestDispatcher request = req.getRequestDispatcher(JSP_IMAGE);
	request.forward(req, resp);
    }

    private boolean handleAction(String action, String fileName, HttpServletResponse resp) throws DataAccessObjectException
    {
	if (action.equalsIgnoreCase(ACTION_DELETE))
	{
	    String fileURI = Environment.getImagesDirectory(getServletContext()) + fileName;
	    deleteFile(fileURI);
	    
	    
	    if( connection == null )
		return false;
	    
	    
	    imageDAO imageDao = new imageDAO( connection);
	    boolean recordDeleted = imageDao.delete(fileName);
	    logger.info("Record for image \'"+fileName+"\' deleted: "+recordDeleted);
	    return recordDeleted;

	}
	
	else if(action.equalsIgnoreCase(ACTION_VIEW))
	{
	    String fileURI = Environment.getImagesDirectory(getServletContext()) + fileName;
	    return respondWithImage(resp, fileURI);
	}
	
	logger.error("Admin Images. Unidentified action from "+JSP_IMAGE+": "+action);
	return false;
    }

    private boolean deleteFile(String file)
    {
	
	boolean fileDeleted = FileUtils.deleteFile(file);
	logger.info("Image \'" + file + "\' deleted: "+fileDeleted);
	return fileDeleted;
    }

    private boolean respondWithImage(HttpServletResponse response, String imageFile)
    {
	String mime = getServletContext().getMimeType(imageFile);
	if (mime == null) mime = DEFAULT_MIME_TYPE;

	File file = new File(imageFile);
	if (!file.exists()) return false;

	logger.info("Displaying image \'" + file + "\'.");

	response.setContentType(mime);
	response.setContentLength((int) file.length());

	FileInputStream in = null;
	OutputStream out = null;
	boolean done = false;
	try
	{
	    in = new FileInputStream(file);
	    out = response.getOutputStream();
	    byte[] buffy = new byte[1024];
	    int sent = 0;
	    while ((sent = in.read(buffy)) >= 0)
	    {
		out.write(buffy, 0, sent);
	    }
	    done = true;
	} catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	    logger.error("FileNotFoundException encountered while displaying image: " + imageFile, e);
	} catch (IOException e)
	{
	    e.printStackTrace();
	    logger.error("IOException encountered while displaying image: " + imageFile, e);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    logger.error("Exception encountered while displaying image: " + imageFile, e);
	} finally
	{
	    try
	    {
		in.close();
		out.close();

	    } catch (IOException e)
	    {
		logger.error("IOException encountered while closing IOStreams.", e);
	    }
	}
	return done;
    }

}
