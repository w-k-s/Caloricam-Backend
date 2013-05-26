package com.wks.CalorieApp.Models;

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
import net.semanticmetadata.lire.utils.FileUtils;

public class Indexer {

	DocumentBuilder documentBuilder;

	public Indexer(DocumentBuilder documentBuilder) {

		if (documentBuilder == null)
			throw new IllegalStateException(
					"DocumentBuilder should not be null");

		this.documentBuilder = documentBuilder;
	}

	public void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}

	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public synchronized boolean indexImage(String imageURI, String indexesDir)
			throws IOException, FileNotFoundException {

		// Configure lucene index writer
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40,
				new WhitespaceAnalyzer(Version.LUCENE_40));

		// create index writer and provide directory where indexes will be
		// saved.
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(
				indexesDir)), config);

		// load image

		BufferedImage image = ImageIO.read(new FileInputStream(imageURI));
		Document document = getDocumentBuilder()
				.createDocument(image, imageURI);
		indexWriter.addDocument(document);

		indexWriter.close();

		return true;
	}

	//TODO there is code repetition here. I could repeatedly call indexImage but 
	//I think repeatedly opening and closing writer streams would be a really bad idea.
	//optimise this to remove repeated code later.
	public synchronized boolean indexImages(String imagesDir, String indexesDir)
			throws IOException {
		// get images
		ArrayList<String> images = FileUtils.getAllImages(new File(imagesDir),
				true);
		// Configure lucene index writer
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40,
				new WhitespaceAnalyzer(Version.LUCENE_40));
		//prepare index writer.
		IndexWriter indexer = new IndexWriter(FSDirectory.open(new File(
				indexesDir)), config);
		// create document for each image and ad to index.
		for (Iterator<String> iterator = images.iterator(); iterator.hasNext();) {
			String imageURI = iterator.next();
			BufferedImage image = ImageIO.read(new FileInputStream(imageURI));
			Document document = getDocumentBuilder().createDocument(image,
					imageURI);
			indexer.addDocument(document);
		}
		// closing the IndexWriter
		indexer.close();
		return true;
	}

}
