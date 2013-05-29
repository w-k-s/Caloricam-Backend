package com.wks.CalorieApp.Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.wks.CalorieApp.DataAccessObjects.ImageDataAccessObject;
import com.wks.CalorieApp.Models.ImageItem;

public class Test extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
       
	resp.setContentType("text/html");
	PrintWriter out = resp.getWriter();
	/*
	FoodDataAccessObject foodsDb = new FoodDataAccessObject(this.getServletContext());
	synchronized(foodsDb)
	{
	    //find
	    FoodItem foodItem = foodsDb.findFoodItemById(1);
	    out.println("Find: "+foodItem+"<br/>");
	    foodItem.setName("Chicken");
	    
	    //update
	    foodsDb.update(foodItem);
	    foodItem = foodsDb.findFoodItemById(1);
	    out.println("Upadte: "+foodItem+"<br/>");
	    
	    //delete
	    foodsDb.delete(1);
	    out.println("deleted<br/>");
	    
	    //read
	    List<FoodItem> foods = foodsDb.read();
	    out.println("Read"+foods+"<br/>");
	    foodItem = new FoodItem();
	    foodItem.setName("Chicken");
	    
	    //create
	    foodsDb.create(foodItem);
	    
	    //read
	    foods = foodsDb.read();
	    out.println("CR: "+foods+"<br/>");
	    
	    
	}*/
	
	ImageDataAccessObject imagesDb = new ImageDataAccessObject(this.getServletContext());
	synchronized(imagesDb)
	{
	  ImageItem image = new ImageItem();
	  image.setImageId("1");
	  image.setFoodId(2);
	  image.setFinalized(false);
	  image.setSize(24);
	  try {
	    imagesDb.create(image);
	} catch (MySQLIntegrityConstraintViolationException e) {
	    e.printStackTrace();
	}
	  out.println("Created.<br/>");
	  
	  ImageItem image2 = imagesDb.findImageItemById("1");
	  out.println("Find: "+image2+"<br/>");
	  
	  List<ImageItem> images= imagesDb.read();
	  out.println("Read: "+images+"<br/>");
	  
	  image.setFinalized(true);
	  try {
	    imagesDb.update(image);
	} catch (MySQLIntegrityConstraintViolationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	  images= imagesDb.read();
	  out.println("Update: "+images+"<br/>");
	  
	  imagesDb.delete(image.getImageId());
	  images= imagesDb.read();
	  out.println("Update: "+images+"<br/>");
	}
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
       super.doPost(req, resp);
    }
    
}
