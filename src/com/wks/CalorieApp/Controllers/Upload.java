package com.wks.CalorieApp.Controllers;

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

import com.wks.CalorieApp.Utils.Environment;
import com.wks.CalorieApp.Utils.FileUtils;
import com.wks.CalorieApp.Utils.JSONHelper;

public class Upload extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_JPG = ".jpg";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	String imagesDir = Environment.getImagesDirectory(getServletContext());

	// set content type
	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	// check that a file is actually being uploaded.
	boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	if (!isMultipart)
	{
	    out.println( JSONHelper.writeStatus(false, Status.FILE_NOT_FOUND.getMessage()) );
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

		    // check file type before uploading it.
		    String extension = fileName.substring(fileName.lastIndexOf("."));
		    if (!extension.equals(EXTENSION_JPEG) && !extension.equals(EXTENSION_JPG))
		    {
			out.println( JSONHelper.writeStatus(false, Status.INVALID_TYPE.getMessage() + ": " + extension) ); 
			return;
		    }

		    boolean fileDidUpload = FileUtils.upload(imagesDir, item);

		    if (fileDidUpload)
		    {
			// forward the request to indexer so that uploaded image
			// can be added to index of images.
			RequestDispatcher indexer = req.getRequestDispatcher("/index/" + fileName);
			indexer.forward(req, resp);
		    } else
			out.println( JSONHelper.writeStatus(false, Status.UPLOAD_FAILED.getMessage()) );
		}
	    }

	} catch (FileUploadException fue)
	{
	    out.println( JSONHelper.writeStatus(false, Status.FILE_NOT_FOUND.getMessage()) );
	} catch (IOException ioe)
	{
	    out.println( JSONHelper.writeStatus(false, Status.IO_ERROR.getMessage()) );  
	} catch(Exception e)
	{
	    out.println( JSONHelper.writeStatus(false, e.getMessage()) );
	}

    }
    
    public enum Status
    {
	UPLOAD_SUCCESSFUL("File uploaded successfully."),
	UPLOAD_FAILED("File Upload failed."),
	IO_ERROR("An error occured while reading the file."),
	NO_FILE_PROVIDED("No file provided."),
	FILE_NOT_FOUND("Uploaded content does not contain a file."),
	INVALID_TYPE("File must be either a JPEG or JPG file.");
	
	private final String message;
	
	Status(String message)
	{
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
    }
}