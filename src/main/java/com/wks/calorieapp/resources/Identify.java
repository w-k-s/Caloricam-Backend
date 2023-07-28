package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.services.IdentificationService;
import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Named
@ApplicationScoped
public class Identify extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "application/json";

    private static final String PARAM_IMAGE_NAME = "image_name";
    private static final String PARAM_MIN_SIMILARITY = "min_similarity";
    private static final String PARAM_MAX_HITS = "max_hits";

    private static Logger logger = Logger.getLogger(Identify.class);

    @Inject
    private IdentificationService identifier;
    @Inject
    @ImagesDirectory
    private File imagesDirectory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(CONTENT_TYPE);
        PrintWriter out = resp.getWriter();

        String imageName = req.getParameter(PARAM_IMAGE_NAME);
        float minSimilarity = req.getParameter(PARAM_MIN_SIMILARITY) == null ? 0f : Float.parseFloat(req.getParameter(PARAM_MIN_SIMILARITY));
        int maximumHits = req.getParameter(PARAM_MAX_HITS) == null ? 0 : Integer.parseInt(req.getParameter(PARAM_MAX_HITS));

        if (imageName == null) {
            out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());
            return;
        }

        File imageFile = new File(imagesDirectory, imageName);
        if (!imageFile.exists()) {
            out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
            logger.error("Index Request Failed. " + imageName + " does not exist.");
            return;
        }

        try {
            Map<String, Float> foodNameSimilarity = identifier.getPossibleFoodsForImage(imageFile.getAbsolutePath(), minSimilarity, maximumHits);

            String jsonMap = JSONValue.toJSONString(foodNameSimilarity);

            out.println(new Response(StatusCode.OK, jsonMap).toJSON());

        } catch (DataAccessObjectException e) {

            logger.error("Failure to load food name from database", e);
            out.println(new Response(StatusCode.DB_SQL_EXCEPTION).toJSON());

        } catch (IOException e) {
            logger.error("IO Exception encountered while finding similar image.", e);
            out.println(new Response(StatusCode.FILE_IO_ERROR).toJSON());
        }

    }

}
