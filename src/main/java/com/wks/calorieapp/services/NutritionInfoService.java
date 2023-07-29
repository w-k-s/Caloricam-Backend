package com.wks.calorieapp.services;

import com.wks.calorieapp.services.fatsecret.FatSecretWebService;
import com.wks.calorieapp.services.fatsecret.entities.NutritionInfo;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Named
@ApplicationScoped
public class NutritionInfoService {

    private static Logger logger = Logger.getLogger(NutritionInfoService.class);

    @Inject
    private FatSecretWebService fatSecretWebService;

    public List<NutritionInfo> searchFood(String foodName) throws IOException {
        return fatSecretWebService.searchFood(foodName);
    }

    public Map<String, List<NutritionInfo>> getNutritionInfoForFoods(Set<String> foodNames) throws IOException {
        Map<String, List<NutritionInfo>> nutritionInfo = new HashMap<String, List<NutritionInfo>>();
        for (String foodName : foodNames) {
            List<NutritionInfo> info = fatSecretWebService.searchFood(foodName);
            if (info != null) {
                nutritionInfo.put(foodName, info);
            }
        }
        return nutritionInfo;
    }
}
