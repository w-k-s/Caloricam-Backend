package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDao;
import com.wks.calorieapp.daos.ImageDao;
import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.factories.ImagesDirectory;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;

/**
 * @author Waqqas
 */
@Named
@ApplicationScoped
public class ImageLinkingService {
    private static Logger logger = Logger.getLogger(ImageLinkingService.class);

    private FoodDao foodDAO;
    private ImageDao imageDAO;

    private File imagesDirectory;

    public ImageLinkingService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public ImageLinkingService(
            FoodDao foodDAO,
            ImageDao imageDAO,
            @ImagesDirectory File imagesDirectory
    ) {
        this.foodDAO = foodDAO;
        this.imageDAO = imageDAO;
        this.imagesDirectory = imagesDirectory;
    }

    /**
     * Links food in image with given food name.
     *
     * @param foodName
     * @param imageName
     * @return true if linking was succesful.
     * @throws DataAccessObjectException
     */
    public boolean linkImageWithFood(String foodName, String imageName) throws DataAccessObjectException, ServiceException {
        File imageFile = new File(imagesDirectory, imageName);
        if (!imageFile.exists()) {
            logger.info("Link request failed. " + imageFile.getAbsolutePath() + " does not exist.");
            throw new ServiceException(ErrorCodes.FILE_NOT_FOUND, imageFile.getAbsolutePath());
        }

        //get id of food in food database
        //getFoodId will create record if it doesnt already exist.
        //likewise for imageId.
        final FoodEntry food = getFoodByName(foodName);
        String imageId = getImageId(imageFile);

        if (food == null || imageId != null) {
            throw new ServiceException(ErrorCodes.FILE_NOT_FOUND);
        }

        final boolean success = linkImageWithFood(imageName, food);
        logger.info("Linker. ImageName: " + imageName + ", FoodId: " + food.getFoodId() + ", Success: " + success);
        return success;
    }

    private FoodEntry getFoodByName(String foodName) throws DataAccessObjectException {
        return foodDAO.read(foodName);
    }

    // This code will insert the image into the db if it hasnt already been
    // inserted.
    private String getImageId(File imageFile) throws DataAccessObjectException {
        ImageEntry imageDto = imageDAO.find(imageFile.getName());
        if (imageDto != null) {
            return imageDto.getImageId();
        }

        imageDto = new ImageEntry();
        imageDto.setImageId(imageFile.getName());
        imageDto.setSize(imageFile.length());
        imageDto.setFinalized(false);
        imageDAO.create(imageDto);

        return imageFile.getName();
    }

    private boolean linkImageWithFood(String imageId, FoodEntry food) throws DataAccessObjectException {
        boolean success = false;
        ImageEntry imageDto = imageDAO.find(imageId);
        if (imageDto != null) {
            imageDto.setFood(food);
            success = imageDAO.update(imageDto);
        }

        return success;
    }
}
