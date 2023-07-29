package com.wks.calorieapp.resources;

import com.wks.calorieapp.services.ErrorCodes;
import com.wks.calorieapp.services.ImageUploadService;
import com.wks.calorieapp.services.ServiceException;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/upload")
public class ImageUploadResource {

    private static class FileUpload {
        String fileName;
        InputStream inputStream;
    }

    private static Logger logger = Logger.getLogger(ImageUploadResource.class);

    @Inject
    private ImageUploadService imageUploadService;

    @POST
    @Path("/image-upload")
    @Consumes("multipart/form-data")
    public Response upload(MultipartFormDataInput input) throws IOException {
        final FileUpload fileUpload = extractFileFromRequest(input);
        if (fileUpload == null) {
            logger.info("Upload Request. " + ErrorCodes.FILE_NOT_FOUND.getDescription().toString());
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorCodes.FILE_NOT_FOUND.getCode(), ErrorCodes.FILE_NOT_FOUND.getDescription()))
                    .build()
            );
        }

        try {
            final boolean success = imageUploadService.upload(fileUpload.fileName, fileUpload.inputStream);
            if (!success) {
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorDto(ErrorCodes.FILE_UPLOAD_FAILED.getCode(), "Upload/Indexing failed"))
                        .build()
                );
            }
            return Response.ok().build();
        } catch (ServiceException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDto(e.getError().getCode(), e.getError().getDescription()))
                    .build()
            );
        } catch (IOException e) {
            logger.error("Upload Request. IOException encountered.", e);
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(ErrorCodes.FILE_IO_ERROR.getCode(), ErrorCodes.FILE_IO_ERROR.getDescription()))
                    .build()
            );
        }
    }

    private FileUpload extractFileFromRequest(MultipartFormDataInput input) throws IOException {
        boolean imageFound = false;
        FileUpload fileUpload = new FileUpload();
        for (Map.Entry<String, List<InputPart>> formPart : input.getFormDataMap().entrySet()) {
            for (InputPart inputPart : formPart.getValue()) {

                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String contentDisposition = headers.getFirst("Content-Disposition");
                if (contentDisposition == null || contentDisposition.isEmpty()) {
                    continue;
                }
                String[] contentDispositionParts = contentDisposition.split(";");
                for (String name : contentDispositionParts) {
                    if (name.trim().startsWith("fileName")) {
                        String[] tmp = name.split("=");
                        fileUpload.fileName = tmp[1].trim().replaceAll("\"", "");
                        break;
                    }
                }

                // Handle the body of that part with an InputStream
                fileUpload.inputStream = inputPart.getBody(InputStream.class, null);
                imageFound = true;
                break;
            }

            if (imageFound) {
                break;
            }
        }

        return imageFound ? fileUpload : null;
    }
}
