package com.wks.calorieapp.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;

public class Indexer
{
    private static Object lock = new Object();
    private static Indexer instance = null;
    private DocumentBuilder documentBuilder;
    private static Logger logger = Logger.getLogger(Indexer.class);

    private Indexer(DocumentBuilder builder)
    {

	if (builder == null) throw new IllegalStateException("DocumentBuilder should not be null");

	this.documentBuilder = builder;

    }

    public static Indexer getInstance(DocumentBuilder builder)
    {
	if (Indexer.instance == null)
	{
	    synchronized (lock)
	    {
		if (Indexer.instance == null)
		{
		    instance = new Indexer(builder);
		}

	    }

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

    public boolean indexImage(File imageFile, File indexesDir) throws IOException
    {
	synchronized(lock)
	{
	    if(imageFile != null && !imageFile.isFile())
		throw new IllegalArgumentException("The Image File provided is not a file.");
	    if(indexesDir != null && !indexesDir.isDirectory())
		throw new IllegalArgumentException("The indexes File provided is not a directory.");
	    
	    
	    ArrayList<String> images = new ArrayList<String>();
	    images.add(imageFile.getAbsolutePath());
	    return this.indexImages(images, indexesDir);
	}
    }

    /**
     * http://www.semanticmetadata.net/wiki/doku.php?id=lire:createindex Indexes
     * Images using the set document builder.
     * 
     * @param imagesDir
     *            directory where images to indexed are kept.
     * @param indexesDir
     *            directory where generated indexes are to be stored
     * @return true, if all images were indexed successfully
     * @throws IOException
     */
    public boolean indexImages(File imagesDir, File indexesDir) throws IOException
    {
	synchronized (lock)
	{
	    if(imagesDir != null && !imagesDir.isDirectory())
		throw new IllegalArgumentException("The Image File provided is not a directory.");
	    if(indexesDir != null && !indexesDir.isDirectory())
		throw new IllegalArgumentException("The indexes File provided is not a directory.");
	    
	    ArrayList<String> images = FileUtils.getAllImages(imagesDir, true);
	    return this.indexImages(images, indexesDir);
	}
    }

    private boolean indexImages(ArrayList<String> images, File indexesDir) throws IOException
    {
	boolean success = false;

	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(Version.LUCENE_40));
	IndexWriter indexer = null;
	Directory indexesDirectory = null;
	try
	{
	    // Initialise indexer with output location.
	    indexesDirectory = FSDirectory.open(indexesDir);
	    indexer = new IndexWriter(indexesDirectory, config);

	    for (Iterator<String> it = images.iterator(); it.hasNext();)
	    {
		String imageFilePath = it.next();

		BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));

		Document document = this.getDocumentBuilder().createDocument(img, imageFilePath);

		indexer.addDocument(document);

	    }

	    success = true;

	} catch (FileNotFoundException e)
	{
	    throw e;
	} catch (LockObtainFailedException e)
	{
	    logger.error("Indexer. Lock ObtainFailedException. Will Unlock", e);
	    if (indexesDirectory != null)
	    {
		IndexWriter.unlock(indexesDirectory);
	    }
	} catch (IOException e)
	{
	    throw e;
	} finally
	{
	    if (indexer != null)
	    {
		try
		{
		    indexer.close();
		} catch (IOException e)
		{
		    IndexWriter.unlock(indexesDirectory);
		}
	    }

	}

	return success;

    }

    /*
     * DocumentBuilder documentBuilder;
     * 
     * public Indexer(DocumentBuilder documentBuilder) {
     * 
     * if (documentBuilder == null) throw new
     * IllegalStateException("DocumentBuilder should not be null");
     * 
     * this.documentBuilder = documentBuilder; }
     * 
     * public void setDocumentBuilder(DocumentBuilder documentBuilder) {
     * this.documentBuilder = documentBuilder; }
     * 
     * public DocumentBuilder getDocumentBuilder() { return documentBuilder; }
     * 
     * public synchronized boolean indexImage(String imageURI, String
     * indexesDir) throws IOException, FileNotFoundException {
     * 
     * 
     * 
     * return true; }
     * 
     * // TODO there is code repetition here. I could repeatedly call indexImage
     * // but // I think repeatedly opening and closing writer streams would be
     * a really // bad idea. // optimise this to remove repeated code later.
     * public synchronized boolean indexImages(String imagesDir, String
     * indexesDir) throws IOException { // get images ArrayList<String> images =
     * FileUtils.getAllImages(new File(imagesDir), true); // Configure lucene
     * index writer IndexWriterConfig config = new
     * IndexWriterConfig(Version.LUCENE_40, new
     * WhitespaceAnalyzer(Version.LUCENE_40)); // prepare index writer.
     * IndexWriter indexer = new IndexWriter(FSDirectory.open(new
     * File(indexesDir)), config); // create document for each image and ad to
     * index. for (Iterator<String> iterator = images.iterator();
     * iterator.hasNext();) { String imageURI = iterator.next(); BufferedImage
     * image = ImageIO.read(new FileInputStream(imageURI)); Document document =
     * getDocumentBuilder().createDocument(image, imageURI);
     * indexer.addDocument(document); } // closing the IndexWriter
     * indexer.close(); return true; }
     */
}
