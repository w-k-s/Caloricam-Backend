package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.services.LinkingService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

public class Link extends HttpServlet {

    private static final long serialVersionUID = -8335463864848081067L;

    private static final Logger logger = Logger.getLogger(Link.class);

    private static final String CONTENT_TYPE = "application/json";

    private static final String PARAM_IMAGE_NAME = "image_name";
    private static final String PARAM_FOOD_NAME = "food_name";

    @Inject
    private LinkingService linker;

    @Inject
    @ImagesDirectory
    private File imagesDir;

    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        PrintWriter out = resp.getWriter();

        String imageName = req.getParameter(PARAM_IMAGE_NAME);
        String foodName = URLDecoder.decode(req.getParameter(PARAM_FOOD_NAME), "UTF-8");

        if (imageName == null || foodName == null || imageName.isEmpty() || foodName.isEmpty()) {
            out.println(new Response(StatusCode.TOO_FEW_ARGS).toJSON());
            return;
        }

        File imageFile = new File(imagesDir, imageName);
        if (!imageFile.exists()) {
            out.println(new Response(StatusCode.FILE_NOT_FOUND).toJSON());
            logger.info("Link request failed. " + imageFile.getAbsolutePath() + " does not exist.");
            return;
        }

        try {
            logger.info("Link Request. Linking " + foodName + " with " + imageFile);
            boolean linked = linker.linkImageWithFood(foodName, imageFile);
            StatusCode statusCode = linked ? StatusCode.OK : StatusCode.LINK_FAILED;
            Response response = new Response(statusCode);
            out.println(response.toJSON());
            logger.info("Link Request. Success: " + linked);
        } catch (DataAccessObjectException e) {
            out.println(new Response(StatusCode.DB_INSERT_FAILED).toJSON());
            logger.error("Link request Failed. Food Item " + foodName + " could not be inserted into db.", e);
        }

    }


}
