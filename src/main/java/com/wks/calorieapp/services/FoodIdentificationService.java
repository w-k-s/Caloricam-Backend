package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.ImageDao;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.factories.IndexesDirectory;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * CITATION
 * <p>
 * Mathias Lux (April 20, 2013), AutoColorCorrelogram Image Feature
 * http://www.semanticmetadata.net/wiki/doku.php?id=lire:autocolorcorrelation
 * (retrieved on 5-Aug-2013)
 */
@Named
@ApplicationScoped
public class FoodIdentificationService {

    private static Logger logger = Logger.getLogger(FoodIdentificationService.class);

    private ImageDao imageDAO;
    private File indexesDirectory;
    private File imagesDirectory;

    @Resource(name = "app/defaults/min-similarity")
    private Float minSimilarity;

    @Resource(name = "app/defaults/max-hits")
    private Integer maxHits;

    public FoodIdentificationService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public FoodIdentificationService(ImageDao imageDAO,
                                     @IndexesDirectory File indexesDirectory,
                                     @ImagesDirectory File imagesDirectory
    ) {
        this.indexesDirectory = indexesDirectory;
        this.imagesDirectory = imagesDirectory;
        this.imageDAO = imageDAO;
    }


    /**
     * @param imageName                  image name
     * @param preferredMinimumSimilarity minimum similarity index
     * @param preferredMaxHits           max results
     * @return Map of food name (key) and similarity index (value)
     * @throws IOException
     * @throws DataAccessObjectException
     */
    public Map<String, Double> getPossibleFoodsForImage(
            final String imageName,
            final Float preferredMinimumSimilarity,
            final Integer preferredMaxHits
    ) throws IOException, DataAccessObjectException, ServiceException {
        return getPossibleFoodsForImage(
                new File(imagesDirectory, imageName),
                preferredMinimumSimilarity,
                preferredMaxHits
        );
    }

    /**
     * @param imageFile                  image file
     * @param preferredMinimumSimilarity minimum similarity index
     * @param preferredMaxHits           max results
     * @return Map of food name (key) and similarity index (value)
     * @throws IOException
     * @throws DataAccessObjectException
     */
    public Map<String, Double> getPossibleFoodsForImage(
            final File imageFile,
            final Float preferredMinimumSimilarity,
            final Integer preferredMaxHits
    ) throws IOException, DataAccessObjectException, ServiceException {

        final float actualMinSimilarity = preferredMinimumSimilarity == null ? minSimilarity : Math.max(preferredMinimumSimilarity, minSimilarity);
        final int actualMaxHits = preferredMaxHits == null ? maxHits : Math.min(preferredMaxHits, maxHits);

        Map<String, Double> foodNameSimilarityMap = new HashMap<>();

        //find similar images
        if (!imageFile.exists()) {
            logger.error("Index Request Failed. " + imageFile.getAbsolutePath() + " does not exist.");
            throw new ServiceException(ErrorCodes.FILE_NOT_FOUND);
        }

        Map<String, Double> imageSimilarityMap = this.findSimilarImages(imageFile.getAbsolutePath(), indexesDirectory.getAbsolutePath(), actualMaxHits);

        //get name of food in each similar image.
        for (java.util.Map.Entry<String, Double> imageSimilarityEntry : imageSimilarityMap.entrySet()) {
            String imageName = imageSimilarityEntry.getKey();
            String foodName = this.getFoodNameForImage(imageName);
            double similarity = imageSimilarityEntry.getValue();

            logger.info(String.format("Food Identification. Request Image: '%s'. Similar Image: '%s', Food Name: '%s'. Similarity: '%f'",
                    imageFile,
                    imageName,
                    foodName,
                    similarity
            ));
            if (foodName != null && !foodName.isEmpty() && (similarity >= actualMinSimilarity)) {
                foodNameSimilarityMap.put(foodName, similarity);
            }
        }

        return foodNameSimilarityMap;
    }

    /**
     * @param imageUri    path to image file
     * @param indexesDir  path to indexes file
     * @param maximumHits maximum number of hits to be determined
     * @return Map of image name (key) and similarity index (value).
     * @throws IOException
     */
    private Map<String, Double> findSimilarImages(String imageUri, String indexesDir, int maximumHits)
            throws IOException {
        //load file into image.
        BufferedImage image = ImageIO.read(new File(imageUri));
        logger.info("Successfully read image " + imageUri);

        //load indexes
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexesDirectory.toURI())));

        //create searcher to compare auto color correlogram descriptors
        ImageSearcher searcher = new GenericFastImageSearcher(maximumHits, AutoColorCorrelogram.class);

        //search for images similar to given image
        logger.info("Searching matching images for for " + imageUri + " using " + searcher + ". Indexes Dir:  " + indexesDir);
        ImageSearchHits hits = searcher.search(image, reader);


        Map<String, Double> imageSimilarityMap = new HashMap<>();

        for (int i = 0; i < hits.length(); i++) {
            String fileName = reader.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];

            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

            imageSimilarityMap.put(fileName, hits.score(i));
        }

        return imageSimilarityMap;
    }

    private String getFoodNameForImage(String imageName) throws DataAccessObjectException {
        ImageEntry imageDTO = imageDAO.find(imageName);
        if (imageDTO == null) {
            return null;
        }
        if (imageDTO.getFood() == null){
            return null;
        }
        return imageDTO.getFood().getName();
    }
}
