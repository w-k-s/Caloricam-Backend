package com.wks.CalorieApp.Controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.wks.CalorieApp.DataAccessObjects.ImageDataAccessObject;
import com.wks.CalorieApp.Models.ImageItem;
import com.wks.CalorieApp.Models.Indexer;
import com.wks.CalorieApp.Utils.Environment;
import com.wks.CalorieApp.Utils.JSONHelper;

public class Index extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String PARAMETER_SEPERATOR = "/";

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

	String imagesDir = Environment.getImagesDirectory(getServletContext());
	String indexesDir = Environment.getIndexesDirectory(getServletContext());

	// check that parameters were provided
	if (req.getPathInfo() == null)
	{
	    // TODO
	    out.println(JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));

	    return;
	}

	// seperate parameters
	String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

	if (parameters.length < 2)
	{
	    out.println(JSONHelper.writeStatus(false, Status.TOO_FEW_ARGS.getMessage()));
	    return;
	}

	String fileName = parameters[1];
	String fileURI = imagesDir + fileName;
	File imageFile = new File(fileURI);

	if (!imageFile.exists())
	{
	    out.println(JSONHelper.writeStatus(false, Status.FILE_NOT_FOUND.getMessage()));
	    return;
	}

	// add image to database
	// Don't index image if you can't record it in db.
	boolean imageIsInserted = false;
	try
	{
	    imageIsInserted = insertImage(imageFile);
	} catch (MySQLIntegrityConstraintViolationException icve)
	{
	    out.println(JSONHelper.writeStatus(false, Status.DB_INTEGRITY_VIOLATION.getMessage()));
	    icve.printStackTrace();
	} catch (Exception e)
	{
	    out.println(JSONHelper.writeStatus(false, Status.DB_INSERT_FAILED.getMessage()));
	}

	if (!imageIsInserted) return;

	try
	{
	    indexImage(fileURI, indexesDir);
	    out.println(JSONHelper.writeStatus(true, Status.INDEXING_SUCCESSFUL.getMessage()));
	} catch (FileNotFoundException fnf)
	{
	    out.println(JSONHelper.writeStatus(false, Status.FILE_NOT_FOUND.getMessage()));
	    fnf.printStackTrace();
	} catch (IOException ioe)
	{
	    out.println(JSONHelper.writeStatus(false, Status.IO_ERROR.getMessage()));
	    ioe.printStackTrace();
	} catch (Exception e)
	{
	    out.println(JSONHelper.writeStatus(false, e.getMessage()));
	    e.printStackTrace();
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
	ImageDataAccessObject imageDb = new ImageDataAccessObject(getServletContext());
	ImageItem imageItem = new ImageItem();
	imageItem.setImageId(imageFile.getName());
	imageItem.setSize(imageFile.length());
	imageItem.setFinalized(false);

	boolean done = imageDb.create(imageItem);
	imageDb.close();
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
