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
import javax.transaction.Transactional;
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
    @Transactional(value = Transactional.TxType.REQUIRED)
    public boolean linkImageWithFood(String foodName, String imageName) throws ServiceException {
        File imageFile = new File(imagesDirectory, imageName);
        if (!imageFile.exists()) {
            logger.info("Link request failed. " + imageFile.getAbsolutePath() + " does not exist.");
            throw new ServiceException(ErrorCodes.FILE_NOT_FOUND, imageFile.getAbsolutePath());
        }

        FoodEntry food;
        try {
            food = getFoodByName(foodName);
            logger.info(String.format("Saved food '%s' with id '%d'", foodName, food.getFoodId()));
        } catch (DataAccessObjectException e) {
            logger.warn(String.format("Food '%s' does not exist in the database and can not be saved.", foodName), e);
            throw new ServiceException(ErrorCodes.DB_INSERT_FAILED, "Failed to save food " + foodName, e);
        }

        try {
            if (getImageId(imageFile) == null) {
                logger.warn("Image '" + imageName + "' not found in database");
                throw new ServiceException(ErrorCodes.FILE_NOT_FOUND);
            }
        } catch (DataAccessObjectException e) {
            logger.warn("Failed to find id for image " + imageFile, e);
            throw new ServiceException(ErrorCodes.DB_SQL_EXCEPTION, "Failed to query database", e);
        }

        try {
            return linkImageWithFood(imageName, food);
        } catch (DataAccessObjectException e) {
            logger.warn("Linker. ImageName: " + imageName + ", FoodId: " + food.getFoodId() + ", Error: " + false, e);
            return false;
        }
    }

    private FoodEntry getFoodByName(String foodName) throws DataAccessObjectException {
        FoodEntry food = foodDAO.read(foodName);
        if (food == null) {
            food = new FoodEntry();
            food.setName(foodName);
            food.setFoodId(foodDAO.create(food));
        }
        return food;
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
