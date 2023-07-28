package com.wks.calorieapp.factories;

import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;

public class IndexesDirectoryFactory {

    @Produces
    @IndexesDirectory
    public File getIndexesDirectory() {
        try {
            InitialContext initialContext = new InitialContext();
            String appRoot = (String) initialContext.lookup("java:comp/env/app/rootdir");
            File indexesDirectory = new File(appRoot, "indexes");
            if (!indexesDirectory.exists()) {
                indexesDirectory.mkdirs();
            }
            return indexesDirectory;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
