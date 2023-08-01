package com.wks.calorieapp.factories;

import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.nio.file.Path;

public class ImagesDirectoryFactory {

    @Produces
    @ImagesDirectory
    public File getImagesDirectory() {
        try {
            InitialContext initialContext = new InitialContext();
            String appRoot = (String) initialContext.lookup("java:comp/env/app/rootdir");
            File imagesDirectory = new File(appRoot, "images");
            if (!imagesDirectory.exists()) {
                imagesDirectory.mkdirs();
            }
            return imagesDirectory;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
