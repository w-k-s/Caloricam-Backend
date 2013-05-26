package com.wks.CalorieApp.Controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import org.json.simple.JSONObject;

import com.wks.CalorieApp.Codes.IndexerCodes;
import com.wks.CalorieApp.Models.Indexer;
import com.wks.CalorieApp.Utils.Environment;

public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "application/json";
	private static final String PARAMETER_SEPERATOR = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		
		String imagesDir = Environment.getImagesDirectory(getServletContext());
		String indexesDir = Environment.getIndexesDirectory(getServletContext());
		
		resp.setContentType(CONTENT_TYPE);
		PrintWriter out = resp.getWriter();
		
		String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);
		
		if(parameters.length<2 )
		{
			outputJSON(out, false, IndexerCodes.TOO_FEW_ARGS.getDescription());
			return;
		}
		
		String fileURI = imagesDir + parameters[1];
		File imageFile = new File(fileURI);
		
		if(!imageFile.exists())
		{
			outputJSON(out,false, IndexerCodes.FILE_NOT_FOUND.getDescription());
			return;
		}
		
		//use auto color correlogram document builder
		DocumentBuilder builder = DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
		Indexer indexer = new Indexer(builder);
		try {
			indexer.indexImage(fileURI, indexesDir);
			outputJSON(out,true,IndexerCodes.INDEXING_SUCCESSFUL.getDescription());
		} catch (FileNotFoundException e) {
			outputJSON(out,false,IndexerCodes.FILE_NOT_FOUND.getDescription());
			e.printStackTrace();
		} catch (IOException e) {
			outputJSON(out,false,IndexerCodes.IO_ERROR.getDescription());
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
}
