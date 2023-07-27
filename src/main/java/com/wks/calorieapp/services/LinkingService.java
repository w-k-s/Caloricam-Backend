package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.FoodDao;
import com.wks.calorieapp.daos.ImageDao;
import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.entities.ImageEntry;
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
public class LinkingService {
    private static Logger logger = Logger.getLogger(LinkingService.class);

    private FoodDao foodDAO;
    private ImageDao imageDAO;

    public LinkingService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public LinkingService(FoodDao foodDAO, ImageDao imageDAO) {
        this.foodDAO = foodDAO;
        this.imageDAO = imageDAO;
    }

    /**
     * Links food in image with given food name.
     *
     * @param foodName
     * @param imageFile
     * @return true if linking was succesful.
     * @throws DataAccessObjectException
     */
    public boolean linkImageWithFood(String foodName, File imageFile) throws DataAccessObjectException {
        boolean success = false;
        String imageName = imageFile.getName();

        //get id of food in food database
        //getFoodId will create record if it doesnt already exist.
        //likewise for imageId.
        long foodId = getFoodId(foodName);
        String imageId = getImageId(imageFile);
        if (foodId != -1 && imageId != null) {
            //set image.foodId = food.id
            success = (linkImageWithFood(imageName, foodId));
            logger.info("Linker. ImageName: " + imageName + ", FoodId: " + foodId + ", Success: " + success);
        }

        return success;
    }

    private long getFoodId(String foodName) throws DataAccessObjectException {
        long foodId = -1;
        FoodEntry foodDto = foodDAO.read(foodName);
        if (foodDto != null) {
            foodId = foodDto.getFoodId();
        } else {
            foodDto = new FoodEntry();
            foodDto.setName(foodName);
            foodId = foodDAO.create(foodDto);
        }
        return foodId;
    }

    // This code will insert the image into the db if it hasnt already been
    // inserted.
    private String getImageId(File imageFile) throws DataAccessObjectException {
        String imageId = null;
        ImageEntry imageDto = imageDAO.find(imageFile.getName());
        if (imageDto != null) {
            imageId = imageDto.getImageId();
        } else {
            imageDto = new ImageEntry();
            imageDto.setImageId(imageFile.getName());
            imageDto.setSize(imageFile.length());
            imageDto.setFinalized(false);
            imageId = imageFile.getName();
        }
        return imageId;
    }

    private boolean linkImageWithFood(String imageId, long foodId) throws DataAccessObjectException {
        boolean success = false;
        ImageEntry imageDto = imageDAO.find(imageId);
        if (imageDto != null) {
            imageDto.setFoodId(foodId);
            success = imageDAO.update(imageDto);
        }

        return success;
    }
}
