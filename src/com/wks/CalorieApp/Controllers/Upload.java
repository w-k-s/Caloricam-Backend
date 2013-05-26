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
import org.json.simple.JSONObject;

import com.wks.CalorieApp.StatusCodes.UploadStatusCode;
import com.wks.CalorieApp.Utils.FileUpload;
import com.wks.CalorieApp.Utils.Environment;

public class Upload extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "application/json";
	private static final String EXTENSION_JPEG = ".jpeg";
	private static final String EXTENSION_JPG = ".jpg";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{

		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{

		String imagesDir = Environment.getImagesDirectory(getServletContext());
		
		// set content type
		resp.setContentType(CONTENT_TYPE);
		PrintWriter out = resp.getWriter();

		// check that a file is actually being uploaded.
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart)
		{
			outputJSON(out, false, UploadStatusCode.FILE_NOT_FOUND.toString());
			return;
		}

		ServletFileUpload upload = new ServletFileUpload();

		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext())
			{
				FileItemStream item = iterator.next();

				// process uploaded image
				if (!item.isFormField())
				{

					String fileName = item.getName();

					//check file type before uploading it.
					String extension = fileName.substring(fileName
							.lastIndexOf("."));
					if (!extension.equals(EXTENSION_JPEG)
							&& !extension.equals(EXTENSION_JPG))
					{
						outputJSON(out, false, UploadStatusCode.INVALID_TYPE + ": "
								+ extension);
						return;
					}

					boolean fileDidUpload = FileUpload.uploadFile(imagesDir,
							item);

					if (fileDidUpload)
					{
						// forward the request to indexer so that uploaded image
						// can be added to index of images.
						RequestDispatcher indexer = req
								.getRequestDispatcher("/index/" + fileName);
						indexer.forward(req, resp);
					} else
						outputJSON(out, false,
								UploadStatusCode.UPLOAD_FAILED.getDescription());
				}
			}

		} catch (FileUploadException fue) {
			outputJSON(out, false, UploadStatusCode.FILE_NOT_FOUND.getDescription());
		} catch (IOException ioe) {
			outputJSON(out, false, ioe.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	private void outputJSON(PrintWriter out, boolean success, String message) {
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("success", success);
		out.println(json);
	}
}