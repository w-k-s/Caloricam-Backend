package com.wks.CalorieApp.Controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wks.CalorieApp.Codes.IndexerCodes;
import com.wks.CalorieApp.Utils.FileUtils;

/*
 * - check this code works
 * - return json containing array of matched images
 * - think of a better JSON writer impmentation.
 * - handle duplication
 */

public class Identify extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "application/json";
	private static final String PARAMETER_SEPERATOR = "/";
	private static final String PARAM_DEFAULT_MAX_HITS = "default_max_hits";

	
	private static String 	imagesDir;
	private static String 	indexesDir;
	//stated here in case of failure to read from web.xml or parse error.
	private static int		defaultMaxHits = 10;
	
	@Override
	public void init() throws ServletException
	{
		
		imagesDir = FileUtils.getImagesDirectory(getServletContext());
		indexesDir = FileUtils.getIndexesDirectory(getServletContext());
		defaultMaxHits = Integer.parseInt(getServletContext().getInitParameter(PARAM_DEFAULT_MAX_HITS));
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType(CONTENT_TYPE);
		PrintWriter out = resp.getWriter();
		
		String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);
		
		if(parameters.length<3 )
		{
			//TODO
			outputJSON(out, false, IndexerCodes.TOO_FEW_ARGS.getDescription());
			return;
		}
		
		//check that maximum number hits provided and value is int
		int maximumHits = defaultMaxHits;
		try{maximumHits = Integer.parseInt(parameters[2]);}
		catch(NumberFormatException nfe)
		{
			//TODO
			outputJSON(out, false, IndexerCodes.TOO_FEW_ARGS.getDescription());
			return;
		}
		
		//check that file exists
		String 	fileURI = imagesDir +  parameters[1];
		File imageFile = new File(fileURI);
		
		
		if(!imageFile.exists())
		{
			outputJSON(out,false, IndexerCodes.FILE_NOT_FOUND.getDescription());
			return;
		}
		
		//load image
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (Exception e) {
			outputJSON(out,false, IndexerCodes.FILE_NOT_FOUND.getDescription());
			e.printStackTrace();
			return;
		}
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexesDir)));
		ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);
		ImageSearchHits hits = searcher.search(image, reader);
		JSONArray matchedImages = new JSONArray();
        for (int i = 0; i < hits.length(); i++) {
            String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            fileName = fileName.substring(fileName.lastIndexOf(File.separator));
            		
            matchedImages.add( hits.score(i) + ":" + fileName);
        }
		outputJSON(out,true,matchedImages.toJSONString());
	}

	private void outputJSON(PrintWriter out, boolean success, String message) {
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("success", success);
		out.println(json);
		
	}
	
}
