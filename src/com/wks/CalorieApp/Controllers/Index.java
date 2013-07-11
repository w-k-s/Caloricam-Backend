package com.wks.calorieapp.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.entities.StatusCode;
import com.wks.calorieapp.services.Indexer;
import com.wks.calorieapp.utils.DatabaseUtils;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.RequestParameterUtil;

public class Index extends HttpServlet
{
    private static final Object lock = new Object();
    private static final long serialVersionUID = 1L;
    private static final String ARG_FORMAT = "/{string: imagename (required) }";
    private static final String CONTENT_TYPE = "application/json";
    private static final int MIN_NUM_PARAMETERS = 1;
    private static String imagesDir = "";
    private static String indexesDir = "";
    private static Logger logger = Logger.getLogger(Index.class);
    private static Connection connection = null;

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

	// seperate parameters
	String fileName = "";
	String[] parameters = RequestParameterUtil.getRequestParameters(req);

	if (parameters == null || parameters.length < MIN_NUM_PARAMETERS)
	{
	    out.println(new Response(StatusCode.TOO_FEW_ARGS, StatusCode.TOO_FEW_ARGS.getDescription(ARG_FORMAT)).toJSON());
	    return;
	} else if (parameters.length > 1) fileName = parameters[1];

	String fileUri = imagesDir + fileName;
	File imageFile = new File(fileUri);

	logger.info("Index Request. Image: " + fileUri);

	if (!imageFile.exists())
	{
	    out.println(new Response(StatusCode.FILE_NOT_FOUND, StatusCode.FILE_NOT_FOUND.getDescription(imageFile.getAbsolutePath())).toJSON());
	    logger.error("Index Request Failed. " + fileUri + " does not exist.");
	    return;
	}

	// add image to database
	// Don't index image if you can't record it in db.
	boolean imageIsInserted = false;
	try
	{
	    imageIsInserted = insertImage(imageFile);
	    logger.info("Index Request. " + fileUri + " has been recorded in the database.");
	} catch (DataAccessObjectException e)
	{
	    out.println(new Response(StatusCode.DB_INTEGRITY_VIOLATION).toJSON());
	    logger.fatal("Index Request. DataAccessObjectException: File:" + fileUri + ". Message: " + e.getMessage(),
		    e);
	}

	if (!imageIsInserted)
	{
	    out.println(new Response(StatusCode.DB_INSERT_FAILED).toJSON());
	    return;
	}

	
	synchronized (lock)
	{
	    Response response = indexImage(fileUri);
	    out.println(response.toJSON());
	}

	
    }

    private Response indexImage(String imageUri)
    {
	IndexWriter indexWriter = null;

	try
	{
	    DocumentBuilder builder = DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
	    // Configure lucene index writer
	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(
		    Version.LUCENE_40));

	    // create index writer and provide directory where indexes will be
	    // saved.
	    indexWriter = new IndexWriter(FSDirectory.open(new File(indexesDir)), config);

	    // load image

	    BufferedImage image = ImageIO.read(new FileInputStream(imageUri));
	    Document document = builder.createDocument(image, imageUri);
	    indexWriter.addDocument(document);

	    logger.info("Index Request. " + imageUri + " has been indexed.");
	    return new Response(StatusCode.OK);

	} catch (FileNotFoundException e)
	{
	    // TODO Auto-generated catch block
	    logger.error("Index Request. File not found exception encountered while indexing: " + imageUri, e);
	    return new Response(StatusCode.FILE_NOT_FOUND, StatusCode.FILE_NOT_FOUND.getDescription(imageUri));
	} catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    logger.error("Index Request. IOException encountered while indexing: " + imageUri, e);
	    return new Response(StatusCode.FILE_IO_ERROR);
	} finally
	{
	    if (indexWriter != null) try
	    {
		indexWriter.close();
	    } catch (IOException e)
	    {
		logger.error("Index Request. IOException encountered while closing IndexWriter: ", e);
		e.printStackTrace();
	    }
	}
    }

    private boolean insertImage(File imageFile) throws DataAccessObjectException
    {
	if (connection == null) return false;

	imageDAO imageDb = new imageDAO(connection);
	ImageEntry imageItem = new ImageEntry();
	imageItem.setImageId(imageFile.getName());
	imageItem.setSize(imageFile.length());
	imageItem.setFinalized(false);

	boolean done = imageDb.create(imageItem);
	return done;
    }
}
