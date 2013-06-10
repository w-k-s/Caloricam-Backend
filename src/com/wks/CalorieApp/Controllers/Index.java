package com.wks.calorieapp.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.wks.calorieapp.daos.ImageDataAccessObject;
import com.wks.calorieapp.models.ImageItem;
import com.wks.calorieapp.models.Indexer;
import com.wks.calorieapp.models.Response;
import com.wks.calorieapp.utils.DatabaseUtil;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class Index extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final int MIN_NUM_PARAMETERS = 1;
    private static String imagesDir = "";
    private static String indexesDir = "";
    private static Logger logger = Logger.getLogger(Index.class);
    private static Connection connection = null;
    

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtil.getConnection();
	imagesDir = Environment.getImagesDirectory(getServletContext());
	indexesDir = Environment.getIndexesDirectory(getServletContext());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();


	// seperate parameters
	String fileName = "";
	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters == null || parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(false, Status.TOO_FEW_ARGS.getMessage()).toJSON());
	    return;
	} else
	    if (parameters.length > 1) fileName = parameters[1];
	
	String fileURI = imagesDir+fileName;
	File imageFile = new File(fileURI);
	
	logger.info("Index Request. Image: "+fileURI);

	if (!imageFile.exists())
	{
	    out.println( new Response(false, Status.FILE_NOT_FOUND.getMessage()).toJSON());
	    logger.error("Index Request Failed. "+fileURI+" does not exist.");
	    return;
	}

	
	// add image to database
	// Don't index image if you can't record it in db.
	boolean imageIsInserted = false;
	try
	{
	    imageIsInserted = insertImage(imageFile);
	    logger.info("Index Request. "+fileURI+" has been recorded in the database.");
	} catch (MySQLIntegrityConstraintViolationException e)
	{
	    out.println( new Response(false, Status.DB_INTEGRITY_VIOLATION.getMessage()).toJSON());
	    logger.fatal("Index Request. Database integrity violated while indexing: "+fileURI,e);
	}catch(SQLException e)
	{
	    out.println(  new Response(false, Status.DB_INSERT_FAILED.toString()).toJSON());
	    logger.error("Index Requst. Invalid SQL statement.",e);
	}

	if (!imageIsInserted)
	{
	    out.println( new Response(false,Status.DB_INSERT_FAILED.toString()).toJSON() );
	    return;
	}

	try
	{
	    indexImage(fileURI, indexesDir);
	    out.println( new Response(true, Status.INDEXING_SUCCESSFUL.getMessage()).toJSON());
	    logger.info("Index Request. "+fileURI+" has been indexed.");
	} catch (FileNotFoundException e)
	{
	    out.println( new Response(false, Status.FILE_NOT_FOUND.getMessage()).toJSON());
	    logger.error("Index Request. File not found exception encountered while indexing: "+fileURI,e);
	} catch (IOException e)
	{
	    out.println( new Response(false, Status.IO_ERROR.getMessage()).toJSON());
	    logger.error("Index Request. IOException encountered while indexing: "+fileURI,e);
	} 

    }

    private void indexImage(String fileURI, String indexesDir) throws FileNotFoundException, IOException
    {
	// use auto color correlogram document builder
	DocumentBuilder builder = DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
	Indexer indexer = new Indexer(builder);

	indexer.indexImage(fileURI, indexesDir);
    }

    private boolean insertImage(File imageFile) throws SQLException
    {
	if(connection == null)
	    return false;
	
	ImageDataAccessObject imageDb = new ImageDataAccessObject(connection);
	ImageItem imageItem = new ImageItem();
	imageItem.setImageId(imageFile.getName());
	imageItem.setSize(imageFile.length());
	imageItem.setFinalized(false);

	boolean done = imageDb.create(imageItem);
	return done;
    }

    enum Status
    {
	INDEXING_SUCCESSFUL("File Indexed Successfully."),
	TOO_FEW_ARGS("Insufficient parameters provided.Service: index/{FileName}"),
	IO_ERROR(""),
	INDEX_ERROR(""),
	IO_INDEX_ERROR("Error occured while reading or indexing image"),
	FILE_NOT_FOUND("Indexing failed because file not found."),
	DB_INTEGRITY_VIOLATION("Database Integrity Violation."),
	DB_INSERT_FAILED("Image could not be added to database.");

	private final String message;

	Status(String message)
	{
	    this.message = message;
	}

	public String getMessage()
	{
	    return message;
	}
    }
}
