package com.wks.CalorieApp.Controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wks.CalorieApp.StatusCodes.LoginStatusCodes;

public class Login extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String ATTR_STATUS = "status";

    @Override
    public void init() throws ServletException {
      //get hashmap of username and passwords here
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	// TODO Auto-generated method stub
	super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	
    	String username = req.getParameter(PARAM_USERNAME);
    	String password = req.getParameter(PARAM_PASSWORD);
    	
    	if(username == null || password == null)
    	{
    	    req.setAttribute(ATTR_STATUS, LoginStatusCodes.NULL_USERNAME_PASSWORD.getDescription());
    	    RequestDispatcher view = req.getRequestDispatcher("login.jsp");
    	    view.forward(req, resp);
    	}
    	
    	
    	if(loginCredentialsAreValid(username,password))
    	{
    	    resp.getWriter().println("OK");
    	}else{
    	    req.setAttribute(ATTR_STATUS, LoginStatusCodes.INCORRECT_USERNAME_PASSWORD.getDescription());
	    RequestDispatcher view = req.getRequestDispatcher("login.jsp");
	    view.forward(req, resp);
    	}
    }

    private boolean loginCredentialsAreValid(String username, String password) {
	//get username and password;
    	String _username = "123";
    	String _password = "kdd";
    	
    	
	return ((username.equals(_username)) && (password.equals(_password)));
    }
}
