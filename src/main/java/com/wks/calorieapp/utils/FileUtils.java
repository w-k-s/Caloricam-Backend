package com.wks.calorieapp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItemStream;

public class FileUtils {
    // This method is synchronised for thread safety.
    // Each request creates a new Servlet Thread.
    // This means multiple threads could access this method at a given time.

    public static synchronized List<String> getFilesInDir(String directory,
                                                          final String[] extensions) {
        List<String> files = new ArrayList<String>();
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {

            for (File file : dir.listFiles()) {
                for (String extension : extensions) {
                    if (file.getName().endsWith(extension))
                        files.add(file.getAbsolutePath());
                }
            }
        }

        return files;
    }

    public static synchronized boolean deleteFile(String fileURI) {
        File file = new File(fileURI);
        if (file.exists() && !file.isDirectory())
            return file.delete();
        return false;
    }

    public static synchronized boolean deleteFiles(List<String> files) {
        int deletedSuccessfully = 0;
        Iterator<String> fileIterator = files.iterator();
        while (fileIterator.hasNext()) {
            if (deleteFile((String) fileIterator.next()))
                deletedSuccessfully++;
        }
        return deletedSuccessfully == files.size();
    }
}
