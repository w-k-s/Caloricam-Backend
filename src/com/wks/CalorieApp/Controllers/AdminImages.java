package com.wks.CalorieApp.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wks.CalorieApp.Utils.*;

public class AdminImages extends HttpServlet
{

    /**
     * 
     */
    private static final long     serialVersionUID   = 1L;

    private static final String   PARAM_ACTION       = "action";
    private static final String   PARAM_IMAGE	= "img";

    private static final String   ACTION_DELETE      = "delete";
    private static final String   ACTION_VIEW	= "view";

    private static final String   ATTR_IMAGE_LIST    = "images";
    private static final String   ATTR_IMAGE_DIR     = "image_dir";
    private static final String   ATTR_AUTHENTICATED = "authenticated";

    private static final String   JSP_IMAGE	  = "/WEB-INF/images.jsp";
    private static final String   SRVLT_LOGIN	= "/login";

    private static final String[] EXTENSIONS	 = { ".jpeg", ".jpg" };
    private static final String   DEFAULT_MIME_TYPE  = "image/jpeg";

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
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(ATTR_AUTHENTICATED);
	    if (b != null) authenticated = b;
	}

	action = req.getParameter(PARAM_ACTION);
	image = req.getParameter(PARAM_IMAGE);

	if (!authenticated)
	{
	    RequestDispatcher login = req.getRequestDispatcher(SRVLT_LOGIN);
	    login.forward(req, resp);
	    return;
	}

	if (action != null && image != null)
	{
	    handleAction(action, image, resp);
	    if (action.equalsIgnoreCase(ACTION_VIEW)) return;
	}

	List<String> files = FileUtils
		.getFilesInDir(
			Environment.getImagesDirectory(getServletContext()),
			EXTENSIONS);

	req.setAttribute(ATTR_IMAGE_LIST, files);
	req.setAttribute(ATTR_IMAGE_DIR,
		Environment.getImagesDirectory(getServletContext()));
	RequestDispatcher request = req.getRequestDispatcher(JSP_IMAGE);
	request.forward(req, resp);
    }

    private void handleAction(String action, String fileName,
	    HttpServletResponse resp) {
	if (action.equalsIgnoreCase(ACTION_DELETE))
	{
	    String fileURI = Environment
		    .getImagesDirectory(getServletContext()) + fileName;
	    deleteFile(fileURI);
	}
	if (action.equalsIgnoreCase(ACTION_VIEW))
	{
	    String fileURI = Environment
		    .getImagesDirectory(getServletContext()) + fileName;
	    respondWithImage(resp, fileURI);
	}
    };

    private boolean deleteFile(String file) {
	return FileUtils.deleteFile(file);
    }

    private boolean respondWithImage(HttpServletResponse response,
	    String imageFile) {
	String mime = getServletContext().getMimeType(imageFile);
	if (mime == null) mime = DEFAULT_MIME_TYPE;

	File file = new File(imageFile);
	if (!file.exists()) return false;

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

	} catch (IOException e)
	{
	    e.printStackTrace();
	} catch (Exception e)
	{
	    e.printStackTrace();
	} finally
	{
	    try
	    {
		in.close();
		out.close();

	    } catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
	return done;
    }

}
