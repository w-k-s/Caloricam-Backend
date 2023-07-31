package com.wks.calorieapp.services;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.factories.ImagesDirectory;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Named
@ApplicationScoped
public class ImageUploadService {

    private static Logger logger = Logger.getLogger(ImageUploadService.class);

    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_JPG = ".jpg";

    private IndexingService indexingService;

    private File imagesDirectory;

    public ImageUploadService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public ImageUploadService(
            IndexingService indexingService,
            @ImagesDirectory File imagesDirectory
    ) {
        this.indexingService = indexingService;
        this.imagesDirectory = imagesDirectory;
    }

    public boolean upload(String fileName, InputStream inputStream) throws ServiceException, IOException {
        logger.info("Upload Request. Uploading: " + fileName);

        String extension = fileName.substring(fileName.lastIndexOf("."));
        if (!extension.equals(EXTENSION_JPEG) && !extension.equals(EXTENSION_JPG)) {
            logger.error("Upload Request. Failed to upload " + fileName + ". Invalid file extension: " + extension);
            throw new ServiceException(ErrorCodes.FILE_TYPE_INVALID, extension);
        }

        File file = new File(imagesDirectory, fileName);
        if(!writeFile(file, inputStream)){
            logger.warn("Upload Request. Failed to save image: " + fileName);
            throw new ServiceException(ErrorCodes.FILE_UPLOAD_FAILED, extension);
        }

        logger.info("Uploaded Request. File uploaded successfully; forwarding request to indexer: " + fileName);
        return indexingService.indexImage(file);
    }

    private boolean writeFile(File file, InputStream inputStream) {
        FileOutputStream fop = null;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);

            if (!file.exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
                logger.info(file.getAbsolutePath() + " does not exist and can not be created");
                return false;
            }
            fop = new FileOutputStream(file);
            fop.write(bytes);
            fop.flush();
            return true;
        } catch (IOException e) {
            logger.error("Failed to write file " + file.getAbsolutePath(), e);
        } finally {
            if (fop != null) {
                try {
                    fop.close();
                } catch (IOException e) {
                    logger.error("Failed to close file output stream", e);
                }
            }
        }
        return false;
    }
}
