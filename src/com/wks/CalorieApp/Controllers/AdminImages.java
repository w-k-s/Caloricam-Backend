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

import org.apache.log4j.Logger;

import com.wks.CalorieApp.Utils.*;

public class AdminImages extends HttpServlet
{
    private static final boolean appIsDeployed = false;
    private static final long serialVersionUID = 1L;

    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_VIEW = "view";

    private static final String JSP_IMAGE = "/WEB-INF/images.jsp";
    private static final String SRVLT_LOGIN = "/login";

    private static final String[] EXTENSIONS = { ".jpeg", ".jpg" };
    private static final String DEFAULT_MIME_TYPE = "image/jpeg";
    private static final String REDIRECT = appIsDeployed?"/":"/calorieapp";
    private static Logger logger = Logger.getLogger(Admin.class);

    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
	    throws javax.servlet.ServletException, java.io.IOException {

	// laods all images, stores them into a list and passes it as an attribute to images.jsp
	
	boolean authenticated = false;
	String action = null;
	String image = null;

	//session is not a thread-safe variable.
	HttpSession session = req.getSession();
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    if (b != null) authenticated = b;
	}

	action = req.getParameter(Parameter.ACTION.toString());
	image = req.getParameter(Parameter.IMAGE.toString());

	if (!authenticated)
	{
	    //redirect to login page
	    resp.sendRedirect(REDIRECT+SRVLT_LOGIN);
	    return;
	}

	if (action != null && image != null)
	{
	    handleAction(action, image, resp);
	    if (action.equalsIgnoreCase(ACTION_VIEW)) return;
	}

	List<String> imageURIList = FileUtils.getFilesInDir(Environment.getImagesDirectory(getServletContext()), EXTENSIONS);

	req.setAttribute(Attribute.IMAGE_LIST.toString(), imageURIList);
	req.setAttribute(Attribute.IMAGE_DIR.toString(), Environment.getImagesDirectory(getServletContext()));
	RequestDispatcher request = req.getRequestDispatcher(JSP_IMAGE);
	request.forward(req, resp);
    }

    private void handleAction(String action, String fileName, HttpServletResponse resp) {
	if (action.equalsIgnoreCase(ACTION_DELETE))
	{
	    String fileURI = Environment.getImagesDirectory(getServletContext()) + fileName;
	    deleteFile(fileURI);
	}
	if (action.equalsIgnoreCase(ACTION_VIEW))
	{
	    String fileURI = Environment.getImagesDirectory(getServletContext()) + fileName;
	    respondWithImage(resp, fileURI);
	}
    };

    private boolean deleteFile(String file) {
	logger.info("image "+file+" deleted.");
	return FileUtils.deleteFile(file);
    }

    private boolean respondWithImage(HttpServletResponse response, String imageFile) {
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
	    logger.error(e);
	} catch (IOException e)
	{
	    e.printStackTrace();
	    logger.error(e);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    logger.error(e);
	} finally
	{
	    try
	    {
		in.close();
		out.close();

	    } catch (IOException e)
	    {
		logger.error(e);
	    }
	}
	return done;
    }

}
