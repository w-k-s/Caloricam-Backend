package com.wks.CalorieApp.Models;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileUpload 
{
	//This method is synchronised for thread safety.
	//Each request creates a new Servlet Thread.
	//This means multiple threads could access this method at a given time.
	
	/**Stores file at given path on server. 
	 * 
	 * @param path absolute path where file is to be saved on server.
	 * @param item FileItemStream 
	 * @return true if uploaded successfully
	 * @throws IOException 
	 */
	public static synchronized boolean uploadFile(String path,org.apache.commons.fileupload.FileItemStream item) throws IOException
	{
			//create file
			File images = new File(path);
			
			if(!images.exists())
				images.mkdirs();
				
			File savedFile = new File(images.getAbsolutePath()+File.separator+item.getName());
			
				
			//if file has already been uploaded to server, abort.
			//if(savedFile.exists())
			//	???
			
			//create input stream to read uploaded file
			InputStream is = item.openStream();
			
			//create output stream to write file to server.
			FileOutputStream fos = new FileOutputStream(savedFile);
			
			//copy bytes from input stream to output stream.
			int data = 0;
			byte[] bytes = new byte[1024];
			while(( data = is.read(bytes))!= -1)
			{
				fos.write(bytes, 0, data);
			}
			
			//close streams
			fos.flush();
			fos.close();
			is.close();
			
			return true;
			
		
		
	}
}
