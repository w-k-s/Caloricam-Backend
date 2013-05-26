package com.wks.CalorieApp.Models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.wks.CalorieApp.Codes.IndexerCodes;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

public class Searcher{

	ImageSearcher searcher;
	
	public Searcher(ImageSearcher searcher)
	{
		this.searcher = searcher;
	}
	
	public void setSearcher(ImageSearcher searcher) {
		this.searcher = searcher;
	}
	
	public ImageSearcher getSearcher() {
		return searcher;
	}
	
	public synchronized String[] findSimilarImages(String imageURI, String indexesDir, int maximumHits) throws IOException
	{
		String[] matchedImages = new String[maximumHits];
		
		BufferedImage image = ImageIO.read(new File(imageURI));
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexesDir)));
		ImageSearchHits hits = getSearcher().search(image, reader);
		
		for (int i = 0; i < hits.length(); i++) {
            String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];		
            matchedImages[i] = hits.score(i) + ":" + fileName;
        }
		
		return matchedImages;
	}
	
}
