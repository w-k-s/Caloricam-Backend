package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.services.ImageLinkingService;
import com.wks.calorieapp.services.ServiceException;
import com.wks.calorieapp.services.ErrorCodes;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/link")
public class ImageLinkingResource {

    private static Logger logger = Logger.getLogger(ImageLinkingResource.class);

    @Inject
    private ImageLinkingService imageLinkingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @QueryParam("food_name") String foodName,
            @QueryParam("image_name") String imageName
    ) {
        if (foodName == null || foodName.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorCodes.TOO_FEW_ARGS.getCode(), "foodName is required"))
                    .build()
            );
        }
        if (imageName == null || imageName.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorCodes.TOO_FEW_ARGS.getCode(), "imageName is required"))
                    .build()
            );
        }

        try {
            final boolean linked = imageLinkingService.linkImageWithFood(foodName, imageName);
            logger.info("Link Request. Success: " + linked);
            if (!linked) {
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorDto(ErrorCodes.LINK_FAILED.getCode(), ErrorCodes.LINK_FAILED.getDescription()))
                        .build()
                );
            }
            return Response.ok().build();
        } catch (DataAccessObjectException e) {
            logger.error("Link request Failed. Food Item " + foodName + " could not be inserted into db.", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.DB_INSERT_FAILED.getCode(), ErrorCodes.DB_INSERT_FAILED.getDescription()))
                    .build()
            );
        } catch (ServiceException e) {
            logger.error("Link request Failed. Food Item " + foodName + " could not be linked.", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getError().getCode(), e.getError().getDescription()))
                    .build()
            );
        }
    }
}
