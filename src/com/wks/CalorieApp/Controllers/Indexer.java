package com.wks.CalorieApp.Controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;

import org.json.simple.JSONObject;

import com.wks.CalorieApp.Codes.IndexerCodes;
import com.wks.CalorieApp.Utils.FileUtils;

public class Indexer extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "application/json";
	private static final String PARAMETER_SEPERATOR = "/";
	
	private static String imagesDir;
	private static String indexesDir;
	
	@Override
	public void init() throws ServletException {
		
		imagesDir = FileUtils.getImagesDirectory(getServletContext());
		indexesDir = FileUtils.getIndexesDirectory(getServletContext());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		doGet(req,resp);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
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
		
		//Configure lucene index writer
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40,
                new WhitespaceAnalyzer(Version.LUCENE_40));
		
		//create index writer and provide directory where indexes will be saved.
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(indexesDir)), config);
		
		//load image
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(fileURI));
			Document document = builder.createDocument(image, fileURI);
			indexWriter.addDocument(document);
			outputJSON(out,true,IndexerCodes.INDEXING_SUCCESSFUL.getDescription());
		} catch (Exception e) {
			outputJSON(out,false,IndexerCodes.IO_INDEX_ERROR.getDescription());
			e.printStackTrace();
		}finally{
			indexWriter.close();
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
