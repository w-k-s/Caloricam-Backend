package com.wks.calorieapp.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDAO;
import com.wks.calorieapp.daos.imageDAO;
import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.entities.ImageEntry;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

public class Identifier
{

    private static Identifier instance = null;
    private static Connection connection;

    private Identifier(Connection connection)
    {
	Identifier.connection = connection;
    }
    
    public static Identifier getInstance(Connection connection)
    {
	if(instance == null)
	{
	    instance = new Identifier(connection);
	}
	
	return instance;
    }

    /**
     * 
     * @param imageUri path of image
     * @param indexesDir path of indexes directory
     * @param minimumSimilarity minimum similarity index
     * @param maximumHits max results
     * @return Map of food name (key) and similarity index (value)
     * @throws IOException
     * @throws DataAccessObjectException
     */
    public  synchronized Map<String,Float> getPossibleFoodsForImage(String imageUri,String indexesDir, float minimumSimilarity, int maximumHits) throws IOException, DataAccessObjectException
    {
	Map<String,Float> foodNameSimilarityMap = new HashMap<String,Float>();
	
	//find similar images
	Map<String,Float> imageSimilarityMap = this.findSimilarImages(imageUri, indexesDir, maximumHits);
	
	//get name of food in each similar image.
	for(java.util.Map.Entry<String, Float> imageSimilarityEntry : imageSimilarityMap.entrySet())
	{
	    String imageName = imageSimilarityEntry.getKey();
	    String foodName = this.getFoodNameForImage(imageName);
	    float similarity = imageSimilarityEntry.getValue();
	    
	    if(foodName != null && !foodName.isEmpty() && (similarity >= minimumSimilarity))
	    {
		foodNameSimilarityMap.put(foodName, similarity);
	    }
	}
	
	return foodNameSimilarityMap;
	
    }

    /**
     * 
     * @param imageUri path to image file
     * @param indexesDir path to indexes file
     * @param maximumHits maximum number of hits to be determined
     * @return Map of image name (key) and similarity index (value).
     * @throws IOException
     */
    private  Map<String,Float> findSimilarImages(String imageUri, String indexesDir, int maximumHits)
	    throws IOException {

	
	Map<String,Float> imageSimilarityMap = new HashMap<String,Float>();

	//load file into image.
	BufferedImage image = ImageIO.read(new File(imageUri));
	
	//load indexes
	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexesDir)));
	
	//create searcher to compare auto color correlogram descriptors
	ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);
	
	//search for images similar to given image
	ImageSearchHits hits = searcher.search(image, reader);

	int limit = hits.length() > maximumHits? maximumHits : hits.length();
	
	for (int i = 0; i < limit; i++)
	{
	    String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
	    
	    fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
	    
	    imageSimilarityMap.put(fileName, hits.score(i));	
	}

	return imageSimilarityMap;
    }

    private  String getFoodNameForImage(String imageName) throws DataAccessObjectException
    {
	String foodName = null;

	if (Identifier.connection != null)
	{
	  
	    imageDAO imageDao = new imageDAO(connection);
	    FoodDAO foodDao = new FoodDAO(connection);
	    
	    ImageEntry imageDTO = imageDao.find(imageName);
	    if (imageDTO != null)
	    {
		FoodEntry foodDTO = foodDao.read(imageDTO.getFoodId());

		if (foodDTO != null) foodName = foodDTO.getName();
	    }

	} else
	    throw new IllegalStateException("Null Connection");

	return foodName;
    }
}
