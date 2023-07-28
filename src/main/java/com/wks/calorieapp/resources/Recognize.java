package com.wks.calorieapp.resources;

import com.wks.calorieapp.api.fatsecret.FatSecretWebService;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;
import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.factories.FatSecretWebServiceFactory;
import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.services.IdentificationService;
import org.apache.log4j.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recognize extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(Identify.class);
    private static final String CONTENT_TYPE = "application/json";

    private static final String PARAM_IMAGE_NAME = "image_name";
    private static final String PARAM_MIN_SIMILARITY = "min_similarity";
    private static final String PARAM_MAX_HITS = "max_hits";

    @Resource(name = "app/defaults/max-hits")
    private int defaultMaxHits;
    @Resource(name = "app/defaults/min-similarity")
    private float defaultMinSimilarity;

    @Inject
    private IdentificationService identifier;

    @Inject
    private FatSecretWebService fatSecretWebService;

    @Inject
    @ImagesDirectory
    private File imagesDir;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        PrintWriter out = resp.getWriter();

        String imageName = req.getParameter(PARAM_IMAGE_NAME);
        float minSimilarity = req.getParameter(PARAM_MIN_SIMILARITY) == null ? 0f : Float.parseFloat(req.getParameter(PARAM_MIN_SIMILARITY));
        int maximumHits = req.getParameter(PARAM_MAX_HITS) == null ? 0 : Integer.parseInt(req.getParameter(PARAM_MAX_HITS));

        if (imageName == null) {
            out.println(new Response(StatusCode.TOO_FEW_ARGS)
                    .toJSON());
            return;
        }

        File imageFile = new File(imagesDir, imageName);
        if (!imageFile.exists()) {
            out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
            logger.error("Index Request Failed. " + imageName + " does not exist.");
            return;
        }

        logger.info("Recognise Request. Image: " + imageFile.getAbsolutePath() + " maximumHits: " + maximumHits);

        Map<String, Float> foodNameSimilarity = getSimilarFoods(imageFile.getAbsolutePath(), minSimilarity, maximumHits);

        Map<String, List<NutritionInfo>> nutritionInfo = getNutritionInfoForFoods(foodNameSimilarity.keySet());
        out.println(new Response(StatusCode.OK, JSONValue.toJSONString(nutritionInfo)).toJSON());

    }

    private Map<String, Float> getSimilarFoods(
            final String fileURI,
            final float preferredMinimumSimilarity,
            final int preferredMaxHits
    ) {
        final float actualMinSimilarity  = Math.max(defaultMinSimilarity, preferredMinimumSimilarity);
        final int actualMaxHits = Math.min(defaultMaxHits, preferredMaxHits);
        Map<String, Float> foodNameSimilarity = null;

        try {
            foodNameSimilarity = identifier.getPossibleFoodsForImage(fileURI, actualMinSimilarity, actualMaxHits);

        } catch (DataAccessObjectException e) {

            logger.error("Recognise request. Failure to load food name from database", e);

        } catch (IOException e) {
            logger.error("Recognise request. IO Exception encountered while recognising.", e);
        }

        return foodNameSimilarity;
    }

    private Map<String, List<NutritionInfo>> getNutritionInfoForFoods(Set<String> foodNames) {
        Map<String, List<NutritionInfo>> nutritionInfo = new HashMap<String, List<NutritionInfo>>();
        for (String foodName : foodNames) {
            try {
                List<NutritionInfo> info = fatSecretWebService.searchFood(foodName);
                if (info != null) {
                    nutritionInfo.put(foodName, info);
                }
            } catch (ParseException e) {
                logger.error("Nutrition Info Request. JSONParser failed to parse JSON: " + foodName, e);

            } catch (IOException e) {
                logger.error("Nutrition Info Request. IOException encontered while retrieving information for: "
                        + foodName, e);

            }
        }

        return nutritionInfo;
    }

}
