package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.wks.calorieapp.entities.Response;
import com.wks.calorieapp.entities.StatusCode;
import com.wks.calorieapp.utils.Environment;
import com.wks.calorieapp.utils.FileUtils;

public class Upload extends HttpServlet
{
    //TODO set size limit
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_JPG = ".jpg";
    private static String imagesDir = "";
    private static Logger logger = Logger.getLogger(Upload.class);

    @Override
    public void init() throws ServletException
    {
	imagesDir = Environment.getImagesDirectory(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	// set content type
	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	// check that a file is actually being uploaded.
	boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	if (!isMultipart)
	{
	    out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
	    logger.info("Upload Request. "+StatusCode.FILE_NOT_FOUND.getDescription().toString());
	    return;
	}

	ServletFileUpload upload = new ServletFileUpload();

	try
	{
	    FileItemIterator iterator = upload.getItemIterator(req);
	    while (iterator.hasNext())
	    {
		FileItemStream item = iterator.next();

		// process uploaded image
		if (!item.isFormField())
		{

		    String fileName = item.getName();
		    logger.info("Upload Request. Uploading: " + fileName);

		    // check file type before uploading it.
		    String extension = fileName.substring(fileName.lastIndexOf("."));
		    if (!extension.equals(EXTENSION_JPEG) && !extension.equals(EXTENSION_JPG))
		    {
			out.println(new Response(StatusCode.FILE_TYPE_INVALID, StatusCode.FILE_TYPE_INVALID.getDescription(extension)).toJSON());
			logger.error("Upload Request. Failed to upload " + fileName + ". Invalid file extension: " + extension);
			return;
		    }

		    boolean fileDidUpload = FileUtils.upload(imagesDir, item);

		    if (fileDidUpload)
		    {
			// forward the request to indexer so that uploaded image
			// can be added to index of images.
			logger.info("Uploaded Request. File uploaded successfully; forwarding request to indexer: " + fileName);
			RequestDispatcher indexer = req.getRequestDispatcher("/index/" + fileName);
			indexer.forward(req, resp);
		    } else
		    {
			out.println(new Response(StatusCode.FILE_UPLOAD_FAILED).toJSON());
			logger.error("Upload Request. File failed to upload: "+fileName);
		    }
		}
	    }

	} catch (FileUploadException e)
	{
	    out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
	    logger.error("Upload Request. FileUploadException encountered.",e);
	} catch (IOException e)
	{
	    out.println(new Response(StatusCode.FILE_IO_ERROR).toJSON());
	    logger.error("Upload Request. IOException encountered.",e);
	}

    }

    
}