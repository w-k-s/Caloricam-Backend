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

import org.json.simple.JSONObject;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.wks.CalorieApp.DataAccessObjects.ImageDataAccessObject;
import com.wks.CalorieApp.Models.ImageItem;
import com.wks.CalorieApp.Models.Indexer;
import com.wks.CalorieApp.StatusCodes.IdentifyStatusCodes;
import com.wks.CalorieApp.StatusCodes.IndexStatusCodes;
import com.wks.CalorieApp.Utils.Environment;

public class Index extends HttpServlet
{

    private static final long   serialVersionUID    = 1L;
    private static final String CONTENT_TYPE	= "application/json";
    private static final String PARAMETER_SEPERATOR = "/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {

	String imagesDir = Environment.getImagesDirectory(getServletContext());
	String indexesDir = Environment
		.getIndexesDirectory(getServletContext());

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	// check that parameters were provided
	if (req.getPathInfo() == null)
	{
	    // TODO
	    outputJSON(out, false,
		    IdentifyStatusCodes.TOO_FEW_ARGS.getDescription());
	    return;
	}

	// seperate parameters
	String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

	if (parameters.length < 2)
	{
	    outputJSON(out, false,
		    IndexStatusCodes.TOO_FEW_ARGS.getDescription());
	    return;
	}

	String fileName = parameters[1];
	String fileURI = imagesDir + fileName;
	File imageFile = new File(fileURI);

	if (!imageFile.exists())
	{
	    outputJSON(out, false,
		    IndexStatusCodes.FILE_NOT_FOUND.getDescription());
	    return;
	}

	// add image to database
	// Don't index image if you can't record it in db.
	boolean imageIsInserted = false;
	try
	{
	    insertImage(imageFile);
	    imageIsInserted = true;
	} catch (MySQLIntegrityConstraintViolationException icve)
	{
	    outputJSON(out, false,
		    IndexStatusCodes.DB_INTEGRITY_VIOLATION.getDescription());
	    icve.printStackTrace();
	} catch (Exception e)
	{
	    outputJSON(out, false,
		    IndexStatusCodes.DB_INSERT_FAILED.getDescription());
	}

	if (!imageIsInserted) return;

	try
	{
	    indexImage(fileURI, indexesDir);
	    outputJSON(out, true,
		    IndexStatusCodes.INDEXING_SUCCESSFUL.getDescription());
	} catch (FileNotFoundException fnf)
	{
	    outputJSON(out, false,
		    IndexStatusCodes.FILE_NOT_FOUND.getDescription());
	    fnf.printStackTrace();
	} catch (IOException ioe)
	{
	    outputJSON(out, false, IndexStatusCodes.IO_ERROR.getDescription());
	    ioe.printStackTrace();
	} catch (Exception e)
	{
	    outputJSON(out, false, e.getMessage());
	    e.printStackTrace();
	}

    }

    @SuppressWarnings("unchecked")
    private void outputJSON(PrintWriter out, boolean success, String message) {
	JSONObject json = new JSONObject();
	json.put("message", message);
	json.put("success", success);
	out.println(json);
    }

    private void indexImage(String fileURI, String indexesDir)
	    throws FileNotFoundException, IOException {
	// use auto color correlogram document builder
	DocumentBuilder builder = DocumentBuilderFactory
		.getAutoColorCorrelogramDocumentBuilder();
	Indexer indexer = new Indexer(builder);

	indexer.indexImage(fileURI, indexesDir);
    }

    private boolean insertImage(File imageFile) throws SQLException {
	ImageDataAccessObject imageDb = new ImageDataAccessObject(
		getServletContext());
	ImageItem imageItem = new ImageItem();
	imageItem.setImageId(imageFile.getName());
	imageItem.setSize(imageFile.length());
	imageItem.setFinalized(false);

	synchronized (imageDb)
	{
	    boolean done = imageDb.create(imageItem);
	    imageDb.close();
	    return done;
	}
    }
}
