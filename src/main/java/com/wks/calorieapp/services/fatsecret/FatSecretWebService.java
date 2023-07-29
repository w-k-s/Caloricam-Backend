package com.wks.calorieapp.services.fatsecret;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.wks.calorieapp.services.fatsecret.entities.FSFoods;
import com.wks.calorieapp.services.fatsecret.entities.FSAbstractResponse;
import com.wks.calorieapp.services.fatsecret.entities.NutritionInfo;
import com.wks.calorieapp.services.fatsecret.factories.FSAbstractResponseFactory;
import com.wks.calorieapp.services.fatsecret.factories.FSResponseFactoryProducer;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

public class FatSecretWebService {
    private FatSecretAPI apiLayer;
    private static final int NUM_TRIES = 3;
    private static Logger logger = Logger.getLogger(FatSecretWebService.class);

    /**
     * Constructor
     *
     * @param consumerKey private key for FatSecret REST API
     * @param sharedKey   public key for FatSecret REST API
     */
    public FatSecretWebService(String consumerKey, String sharedKey) {
        this.apiLayer = new FatSecretAPI(consumerKey, sharedKey);
    }

    /**
     * Searches for food in FatSecret food database
     *
     * @param foodName food name
     * @return list of nutrition info for matching items
     * @throws IOException
     * @throws
     */
    public List<NutritionInfo> searchFood(String foodName) throws IOException {
        //Weird Problem:
        //Due to the nonce, the OAuth signature isn't always accepted.
        //Multiple tries need to be made.
        try {
            List<NutritionInfo> nutritionInfoList = new ArrayList<NutritionInfo>();
            for (int i = 0; i < NUM_TRIES; i++) {
                logger.info("Searching for " + foodName + " on fatsecret. Try " + i);
                String json = this.apiLayer.foodsSearch(foodName);
                logger.info("FatSecret Response: " + json);
                FSAbstractResponseFactory factory = FSResponseFactoryProducer.getFactory(json);

                FSAbstractResponse response = factory.createResponseFromJSON(json);
                if (response instanceof FSFoods) {
                    logger.info("FSFoods: " + response);
                    nutritionInfoList = ((FSFoods) response).getNutritionInfoList();
                    break;
                }
            }

            return nutritionInfoList;
        } catch (ParseException e) {
            logger.error("Failed to parse fatsecret response", e);
            return Collections.emptyList();
        }
    }
}
