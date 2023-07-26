package com.wks.calorieapp.resources;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@SuppressWarnings("unused")
public class Test extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(Test.class);

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
	/*
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
	  
	  ImageItem image2 = imagesDb.find("1");
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
	}*/
	/*
	UserDataAccessObject usersDb = new UserDataAccessObject(getServletContext());
	synchronized(usersDb)
	{
	    Map<String,User> users = usersDb.read();
	    User user = new User("Test","123");
	    usersDb.delete(user);
	    users = usersDb.read();
	    resp.getWriter().println("delte: "+users);
	}
	*/
	
	/*
	try {
	    FatSecretAPI fatsecret = new FatSecretAPI("**********************","**********************");
	    String json = fatsecret.foodsSearch("chicken");
	    
	    
	    
	    List<FoodInfoItem> foods = FoodInfoItemFactory.createFoodItemsFromJSON(json);
	    //out.println(foods);
	    //out.println(json);
	    
	    FoodInfoItem food = foods.get(1);
	    out.println(food.getDescription());
	    
	} catch (FatSecretException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}*/
	/*
	
	try{
	    int s = Integer.parseInt("s");
	}catch(NumberFormatException e)
	{
	    logger.error(e);
	}*/
	/*
	Connection connection = DatabaseUtil.getConnection();
	GeneralDataAccessObject gdao = new GeneralDataAccessObject(connection);
	boolean b = gdao.doQuery("INSERT INTO Users VALUES ('wks','2212')");
	
	
	FSWebService ws = new FSWebService("**********************","*****************");
	try
	{
	    List<NutritionInfo> l = ws.searchFood("chicken");
	    System.out.println(l);
	} catch (ParseException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}*/
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
       super.doPost(req, resp);
    }
    
}
