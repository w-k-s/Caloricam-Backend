package com.wks.calorieapp.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.utils.FileUtils;

public class Indexer
{
    private static Indexer instance = null;
    private DocumentBuilder documentBuilder;

    private Indexer(DocumentBuilder builder)
    {

	if (documentBuilder == null)
	    throw new IllegalStateException("DocumentBuilder should not be null");

	this.documentBuilder = builder;
	
    }

    public static Indexer getInstance(DocumentBuilder builder)
    {
	if(Indexer.instance == null)
	{
	    instance = new Indexer(builder);
	}
	
	return instance;
    }
    
    public void setDocumentBuilder(DocumentBuilder documentBuilder)
    {
	this.documentBuilder = documentBuilder;
    }

    public DocumentBuilder getDocumentBuilder()
    {
	return documentBuilder;
    }

    public synchronized boolean indexImages(String imagesDir, String indexesDir) throws IOException
    {
	//get filepaths for all images
	ArrayList<String> images = FileUtils.getAllImages(new File(imagesDir), true);
	
	//Configure Lucene IndexWriter.
	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(Version.LUCENE_40));
	
	IndexWriter indexer = null;
	try
	{
	    //Initialise indexer with output location.
	    indexer = new IndexWriter(FSDirectory.open(new File(indexesDir)), config);
	    
	    // Read each image file into a buffered image.
	    // Create Lucene Document from image
	    // Index Lucene Document.
	    for (Iterator<String> iterator = images.iterator(); iterator.hasNext();)
	    {
	        String imageURI = iterator.next();
	        BufferedImage image = ImageIO.read(new FileInputStream(imageURI));
	        DocumentBuilder builder = this.getDocumentBuilder();
	        Document document = builder.createDocument(image, imageURI);
	        indexer.addDocument(document);
	    }
	    return true;
	} catch (FileNotFoundException e)
	{
	    throw e;
	} catch (IOException e)
	{
	    throw e;
	}finally{
	    if(indexer != null)
	    {
		 try
		{
		    indexer.close();
		} catch (IOException e)
		{
		   throw e;
		}
	    }
	   
	}
	
    }

    
}
