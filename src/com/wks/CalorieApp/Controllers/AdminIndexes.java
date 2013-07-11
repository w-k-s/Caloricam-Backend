package com.wks.calorieapp.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import com.wks.calorieapp.services.Indexer;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.FileUtils;

public class AdminIndexes extends HttpServlet
{

    private static final long serialVersionUID = 4863024199042688296L;
    private final static String ACTION_DELETE = "delete";
    private final static String ACTION_REINDEX = "reindex";

    private final static String JSP_INDEXES = "/WEB-INF/indexes.jsp";
    private final static String REDIRECT = "/calorieapp";
    private final static String SRVLT_LOGIN = "/login";

    private static Logger logger = Logger.getLogger(AdminIndexes.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	boolean authenticated = false;
	String username = "";
	String action = null;

	HttpSession session = req.getSession();
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    username = (String) session.getAttribute(Attribute.USERNAME.toString());
	    if (b != null) authenticated = b;
	}

	if (!authenticated)
	{
	    logger.info("Admin Index. Page requested. User not authenticated");
	    resp.sendRedirect(REDIRECT + SRVLT_LOGIN);
	    return;
	} else
	{
	    logger.info("Admin Index. Page requested by "+username);
	}

	action = req.getParameter(Parameter.ACTION.toString());
	logger.info("Admin Index. action = '" + action + "'.");
	if (action != null)
	{
	    boolean success = handleAction(action);
	    logger.info("Admin Index. action='"+action+"', success='"+success+"'");
	}
	

	req.setAttribute(Attribute.INDEX_LIST.toString(), getIndexFilesList());
	RequestDispatcher indexView = req.getRequestDispatcher(JSP_INDEXES);
	indexView.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	doGet(req, resp);
    }

    private List<String> getIndexFilesList()
    {
	return FileUtils.getFilesInDir(Environment.getIndexesDirectory(getServletContext()), new String[] { "" });
    }
    
    private boolean handleAction(String action)
    {
	if(action.equalsIgnoreCase(ACTION_DELETE))
	{
	    return deleteIndexes();
	}else if(action.equalsIgnoreCase(ACTION_REINDEX))
	{
	    try
	    {
		return reindex();
	    } catch (FileNotFoundException e)
	    {
		logger.error("Admin Index. FileNotFoundException encountered while reindexing.", e);
		e.printStackTrace();
	    } catch (IOException e)
	    {
		logger.error("Admin Index. IOException encountered while reindexing.", e);
		e.printStackTrace();
	    }
	    return false;
	}else
	{
	    logger.error("Admin index. Unidentified action from "+JSP_INDEXES+": "+action);
	    return false;
	}
    }
    
    private boolean deleteIndexes()
    {
	String indexesDir = Environment.getIndexesDirectory(getServletContext());
	List<String> fileNames = FileUtils.getFilesInDir(Environment.getIndexesDirectory(getServletContext()), new String[]{""});
	List<String> fileUri = new ArrayList<String>();
	for(String fileName : fileNames)
	    fileUri.add(indexesDir + fileName);
	
	return FileUtils.deleteFiles(fileUri);
    }
    
    private boolean reindex() throws FileNotFoundException, IOException
    {
	DocumentBuilder builder = DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
	Indexer indexer = new Indexer(builder);

	return indexer.indexImage(Environment.getImagesDirectory(getServletContext()), Environment.getIndexesDirectory(getServletContext()));
    }
}
