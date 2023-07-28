package com.wks.calorieapp.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wks.calorieapp.factories.FatSecretWebServiceFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.wks.calorieapp.api.fatsecret.FatSecretWebService;
import com.wks.calorieapp.api.fatsecret.entities.NutritionInfo;

public class GetNutritionInfo extends HttpServlet {

    private static final long serialVersionUID = 2084144039896224805L;
    private static final String CONTENT_TYPE = "application/json";
    private static Logger logger = Logger.getLogger(GetNutritionInfo.class);

    private static final String PARAM_FOOD_NAME = "food_name";

    @Inject
    private FatSecretWebService fatSecretWebService;

    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(CONTENT_TYPE);
        PrintWriter out = resp.getWriter();

        String foodName = URLDecoder.decode(req.getParameter(PARAM_FOOD_NAME), "UTF-8");

        if (foodName == null || foodName.isEmpty()) {
            out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());
            return;
        }

        logger.info("Nutrition Info Request. Finding Nutrition information for " + foodName);

        try {
            List<NutritionInfo> nutritionInfo = fatSecretWebService.searchFood(foodName);
            out.println(new Response(StatusCode.OK, JSONValue.toJSONString(nutritionInfo)).toJSON());
        } catch (ParseException e) {
            Response response = new Response(StatusCode.PARSE_ERROR);
            response.setMessage(StatusCode.PARSE_ERROR.getDescription() + ", FoodName: " + foodName);

            logger.error("Nutrition Info Request. JSONParser failed to parse JSON: " + foodName, e);
            out.println(response.toJSON());

        } catch (IOException e) {
            logger.error(
                    "Nutrition Info Request. IOException encontered while retrieving information for: " + foodName, e);
            out.println(new Response(StatusCode.FILE_IO_ERROR).toJSON());

        }

    }

}
