package com.wks.calorieapp.resources;

import com.wks.calorieapp.services.NutritionInfoService;
import com.wks.calorieapp.services.fatsecret.entities.NutritionInfo;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/nutrition_info")
public class NutritionInfoResource {

    private static Logger logger = Logger.getLogger(NutritionInfoResource.class);

    @Inject
    private NutritionInfoService nutritionInfoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<NutritionInfo> get(@QueryParam("food_name") String foodName) throws IOException {
        if (foodName == null || foodName.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                            .entity("foodName parameter is mandatory")
                            .build()
            );
        }
        logger.info("Nutrition Info Request. Finding Nutrition information for " + foodName);
        return nutritionInfoService.searchFood(foodName);
    }

}
