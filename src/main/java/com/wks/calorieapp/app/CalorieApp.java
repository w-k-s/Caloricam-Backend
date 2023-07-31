package com.wks.calorieapp.app;

import com.wks.calorieapp.resources.FoodIdentificationResource;
import com.wks.calorieapp.resources.FoodRecognitionResource;
import com.wks.calorieapp.resources.ImageIndexingResource;
import com.wks.calorieapp.resources.ImageLinkingResource;
import com.wks.calorieapp.resources.ImageUploadResource;
import com.wks.calorieapp.resources.NutritionInfoResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(value = "/api")
public class CalorieApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(
                FoodIdentificationResource.class,
                FoodRecognitionResource.class,
                ImageIndexingResource.class,
                ImageLinkingResource.class,
                ImageUploadResource.class,
                NutritionInfoResource.class
        ));
    }
}
