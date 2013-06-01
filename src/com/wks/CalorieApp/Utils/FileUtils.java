package com.wks.CalorieApp.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItemStream;

public class FileUtils {
    // This method is synchronised for thread safety.
    // Each request creates a new Servlet Thread.
    // This means multiple threads could access this method at a given time.

    /**
     * Stores file at given path on server.
     * 
     * @param path
     *            absolute path where file is to be saved on server.
     * @param item
     *            FileItemStream
     * @return true if uploaded successfully
     * @throws IOException
     */
    public static synchronized boolean upload(String path, FileItemStream item)
	    throws IOException {
	// create file
	File images = new File(path);

	if (!images.exists())
	    images.mkdirs();

	File savedFile = new File(images.getAbsolutePath() + File.separator
		+ item.getName());

	// if file has already been uploaded to server, abort.
	// if(savedFile.exists())
	// ???

	// create input stream to read uploaded file
	InputStream is = item.openStream();

	// create output stream to write file to server.
	FileOutputStream fos = new FileOutputStream(savedFile);

	// copy bytes from input stream to output stream.
	int data = 0;
	byte[] bytes = new byte[1024];
	while ((data = is.read(bytes)) != -1) {
	    fos.write(bytes, 0, data);
	}

	// close streams
	fos.flush();
	fos.close();
	is.close();

	return true;
    }

    public static synchronized List<String> getFilesInDir(String directory,
	    final String[] extensions) {
	List<String> files = new ArrayList<String>();
	File dir = new File(directory);
	if (dir.exists() && dir.isDirectory()) {

	    for (File file : dir.listFiles())
	    {
		for(String extension : extensions)
		{
		    if(file.getName().endsWith(extension))
			files.add( file.getName());
		}
	    }
	}

	return files;
    }
}
