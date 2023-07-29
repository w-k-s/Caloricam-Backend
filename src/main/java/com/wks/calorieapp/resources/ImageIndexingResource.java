package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.services.ErrorCodes;
import com.wks.calorieapp.services.IndexingService;
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

@Path("/index")
public class ImageIndexingResource {

    private static Logger logger = Logger.getLogger(ImageIndexingResource.class);

    @Inject
    private IndexingService indexingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @QueryParam("image_name") String imageName
    ) {
        if (imageName == null || imageName.isEmpty()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorCodes.TOO_FEW_ARGS.getCode(), "imageName is required"))
                    .build()
            );
        }

        logger.info("Index Request. Image: " + imageName);

        try {
            long start = System.currentTimeMillis();
            indexingService.indexImage(imageName);
            logger.info("Index Request. Indexing complete in " + (System.currentTimeMillis() - start) + " ms.");
            return Response.ok().build();
        } catch (IOException e) {
            logger.error("image file error", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.FILE_IO_ERROR.getCode(), ErrorCodes.FILE_IO_ERROR.getDescription()))
                    .build()
            );
        } catch (ServiceException e) {
            logger.error("Failed to index image", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.INDEX_ERROR.getCode(), ErrorCodes.INDEX_ERROR.getDescription()))
                    .build()
            );
        } catch (DataAccessObjectException e) {
            logger.error("Failed to insert image", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.DB_INSERT_FAILED.getCode(), ErrorCodes.DB_INSERT_FAILED.getDescription()))
                    .build()
            );
        }
    }
}
