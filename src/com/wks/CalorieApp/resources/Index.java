package com.wks.calorieapp.resources;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.services.Indexer;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;

public class Index extends HttpServlet
{
    

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    
    private static final String PARAM_IMAGE_NAME = "image_name";
    
    private static Connection connection = null;
    private static String imagesDir = "";
    private static String indexesDir = "";
    private static Logger logger = Logger.getLogger(Index.class);
    

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtils.getConnection();
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

	String imageName = req.getParameter(PARAM_IMAGE_NAME);

	if (imageName == null || imageName.isEmpty())
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());
	    return;
	}

	File imageFile = new File(imagesDir + imageName);

	if (!imageFile.exists())
	{
	    Response response = new Response(StatusCode.FILE_NOT_FOUND);
	    response.setMessage(StatusCode.FILE_NOT_FOUND.getDescription()+" : "+imageFile.getAbsolutePath());
	    out.println(response.toJSON());
	    logger.error("Index Request Failed. " + imageName + " does not exist.");
	    return;
	}

	logger.info("Index Request. Image: " + imageFile.getAbsolutePath());

	if (this.insertImage(imageFile))
	{
	    long start = System.currentTimeMillis();
	    boolean success = this.indexImage(imageFile);
	    logger.info("Index Request. Indexing complete in "+(System.currentTimeMillis() - start)+" ms.");

	    StatusCode statusCode = success ? StatusCode.OK : StatusCode.INDEX_ERROR;
	    Response response = new Response(statusCode);
	    out.println(response.toJSON());
	} else
	{
	    out.println(new Response(StatusCode.DB_INSERT_FAILED).toJSON());
	    return;
	}

    }

    private boolean indexImage(File imageFile)
    {
	return this.indexImage(imageFile.getAbsolutePath());
    }

    /**Indexes the image
     * 
     * @param imageUri path to the image file.
     * @return true if image was indexed successfully.
     */
    private boolean indexImage(String imageUri)
    {
	boolean success = false;
	
	try
	{
	    
	    //Use auto color correlogram method to build the documents
	    DocumentBuilder builder = DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
	    
	    //TODO changed
	    //get instace of indexer
	    Indexer indexer = Indexer.getInstance(builder);
	    //Indexer indexer = new Indexer(builder);
	    
	    //index all images in imagesDir. Output to indexes dir.
	    long startIndex = System.currentTimeMillis();
	    success = indexer.indexImages(Index.imagesDir, Index.indexesDir);
	    logger.info("Index Request. Total Indexing Time: "+(System.currentTimeMillis() - startIndex)+" ms.");
	    
	} catch (IOException e)
	{
	    logger.error("Index Request. Image: "+imageUri,e);
	}

	return success;
    }

    private boolean insertImage(File imageFile)
    {
	if (connection == null) return false;

	boolean success = true;

	try
	{
	    imageDAO imageDb = new imageDAO(connection);

	    if (imageDb.find(imageFile.getName()) == null)
	    {

		ImageEntry imageItem = new ImageEntry();
		imageItem.setImageId(imageFile.getName());
		imageItem.setSize(imageFile.length());
		imageItem.setFinalized(false);

		success = imageDb.create(imageItem);
	    } else
	    {
		success = true;
	    }
	} catch (DataAccessObjectException e)
	{
	    logger.error("Index Required. Failed to insert image record.", e);
	    e.printStackTrace();
	}

	return success;
    }
}
