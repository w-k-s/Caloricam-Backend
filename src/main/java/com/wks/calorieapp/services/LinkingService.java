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
        final FoodEntry food = getFoodByName(foodName);
        String imageId = getImageId(imageFile);
        if (food != null && imageId != null) {
            //set image.foodId = food.id
            success = (linkImageWithFood(imageName, food));
            logger.info("Linker. ImageName: " + imageName + ", FoodId: " + food.getFoodId() + ", Success: " + success);
        }

        return success;
    }

    private FoodEntry getFoodByName(String foodName) throws DataAccessObjectException {
        return foodDAO.read(foodName);
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
