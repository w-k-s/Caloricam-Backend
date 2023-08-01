package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.services.ErrorCodes;
import com.wks.calorieapp.services.FoodIdentificationService;
import com.wks.calorieapp.services.ServiceException;
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
import java.util.Map;

@Path("/identify")
public class FoodIdentificationResource {

    private static Logger logger = Logger.getLogger(FoodIdentificationResource.class);

    @Inject
    private FoodIdentificationService foodIdentificationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Double> get(
            @QueryParam("image_name") String imageName,
            @QueryParam("min_similarity") Float minSimilarity,
            @QueryParam("max_hits") Integer maxHits
    ) {
        if (imageName == null || imageName.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorCodes.TOO_FEW_ARGS.getCode(), "imageName is required"))
                    .build()
            );
        }

        try {
            return foodIdentificationService
                    .getPossibleFoodsForImage(
                            imageName,
                            minSimilarity,
                            maxHits
                    );
        } catch (IOException e) {
            logger.error("IO Exception encountered while finding similar image.", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.FILE_IO_ERROR.getCode(), ErrorCodes.FILE_IO_ERROR.getDescription()))
                    .build()
            );
        } catch (DataAccessObjectException e) {
            logger.error("Failure to load food name from database", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.DB_SQL_EXCEPTION.getCode(), ErrorCodes.DB_SQL_EXCEPTION.getDescription()))
                    .build()
            );
        } catch (ServiceException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getError().getCode(), e.getError().getDescription()))
                    .build()
            );
        }
    }
}
