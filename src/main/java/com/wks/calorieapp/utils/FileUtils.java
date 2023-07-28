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

    /**
     * Stores file at given path on server.
     *
     * @param path absolute path where file is to be saved on server.
     * @param item FileItemStream
     * @return true if uploaded successfully
     * @throws IOException
     */
    public static synchronized boolean upload(String path, FileItemStream item) throws IOException {

        // create file
        File images = new File(path);

        if (!images.exists())
            images.mkdirs();

        File savedFile = new File(images.getAbsolutePath() + File.separator
                + item.getName());

        if (!savedFile.exists()) {
            // create input stream to read uploaded file
            InputStream is = null;
            // create output stream to write file to server.
            FileOutputStream fos = null;
            try {
                is = item.openStream();

                fos = new FileOutputStream(savedFile);

                // copy bytes from input stream to output stream.
                int data = 0;
                byte[] bytes = new byte[1024];
                while ((data = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, data);
                }
            } catch (FileNotFoundException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }

                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    throw e;
                }
            }

        }


        return true;
    }

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
