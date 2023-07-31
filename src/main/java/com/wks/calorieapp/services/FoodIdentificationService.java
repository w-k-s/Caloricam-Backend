package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.ImageDao;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.factories.IndexesDirectory;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
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
    public Map<String, Float> getPossibleFoodsForImage(
            final String imageName,
            final float preferredMinimumSimilarity,
            final int preferredMaxHits
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
    public Map<String, Float> getPossibleFoodsForImage(
            final File imageFile,
            final float preferredMinimumSimilarity,
            final int preferredMaxHits
    ) throws IOException, DataAccessObjectException, ServiceException {

        final float actualMinSimilarity = Math.max(preferredMinimumSimilarity, minSimilarity);
        final int actualMaxHits = Math.min(preferredMaxHits, maxHits);

        Map<String, Float> foodNameSimilarityMap = new HashMap<String, Float>();

        //find similar images
        if (!imageFile.exists()) {
            logger.error("Index Request Failed. " + imageFile.getAbsolutePath() + " does not exist.");
            throw new ServiceException(ErrorCodes.FILE_NOT_FOUND);
        }

        Map<String, Float> imageSimilarityMap = this.findSimilarImages(imageFile.getAbsolutePath(), indexesDirectory.getAbsolutePath(), actualMaxHits);

        //get name of food in each similar image.
        for (java.util.Map.Entry<String, Float> imageSimilarityEntry : imageSimilarityMap.entrySet()) {
            String imageName = imageSimilarityEntry.getKey();
            String foodName = this.getFoodNameForImage(imageName);
            float similarity = imageSimilarityEntry.getValue();

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
    private Map<String, Float> findSimilarImages(String imageUri, String indexesDir, int maximumHits)
            throws IOException {


        Map<String, Float> imageSimilarityMap = new HashMap<String, Float>();

        //load file into image.
        BufferedImage image = ImageIO.read(new File(imageUri));

        //load indexes
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexesDir)));

        //create searcher to compare auto color correlogram descriptors
        ImageSearcher searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(maximumHits);

        //search for images similar to given image
        ImageSearchHits hits = searcher.search(image, reader);

        int limit = Math.min(hits.length(), maximumHits);

        for (int i = 0; i < limit; i++) {
            String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];

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
        return imageDTO.getFood().getName();
    }
}
