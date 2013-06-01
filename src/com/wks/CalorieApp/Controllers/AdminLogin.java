package com.wks.CalorieApp.Controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wks.CalorieApp.DataAccessObjects.UserDataAccessObject;
import com.wks.CalorieApp.Models.User;
import com.wks.CalorieApp.StatusCodes.LoginStatusCodes;

public class AdminLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_AUTHENTICATED = "authenticated";
    private static final String ATTR_USERNAME = PARAM_USERNAME;
    
    private static final String JSP_LOGIN = "/WEB-INF/login.jsp";
    
    //Following a strict MVC pattern i.e. one servlet for each jsp
    //only the admin servlet is allowed to load the admin.jsp
    //so this servlet will redirect to admin servlet instead of loading the admin.jsp.
    private static final String SRVLT_ADMIN = "/admin";
    private static final String REDIRECT = "/calorieapp";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	// TODO Auto-generated method stub
	doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {

	//get all parameters
	boolean authenticated = false;
	String username = null;
	String password = null;
	
	HttpSession session = req.getSession();
	synchronized(session){
	    Boolean b = (Boolean )session.getAttribute(ATTR_AUTHENTICATED);
	   if(b!= null)
	       authenticated = b;
	}
	
	username = req.getParameter(PARAM_USERNAME);
	password = req.getParameter(PARAM_PASSWORD);
	
	//check if user is already signed in
	if(authenticated)
	{
	    RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
	    admin.forward(req, resp);
	    return;
	}
	
	//if username and password submitted, validate
	if(username!= null && password != null)
	{
	    if(loginCredentialsAreValid(username,password))
	    {
		synchronized(session){
		    session.setAttribute(ATTR_AUTHENTICATED, true);
		    session.setAttribute(ATTR_USERNAME, username);
		}
		
		RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
		admin.forward(req, resp);
		return;
	    }else{
		req.setAttribute(ATTR_STATUS, LoginStatusCodes.INCORRECT_USERNAME_PASSWORD.getDescription());
		RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
		login.forward(req, resp);
		return;
	    }
	}else{
	    req.removeAttribute(ATTR_STATUS);
	    RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
	    login.forward(req, resp);
	    return;
	}
	
	
    }

    private boolean loginCredentialsAreValid(String username, String password)
    {
	UserDataAccessObject usersDb = new UserDataAccessObject(getServletContext());
	User user = null;
	
	synchronized(usersDb)
	{
	    user = usersDb.find(username);
	}
	
	if(user == null)
	    return false;
	
	if(user.getPassword().equals(password))
	    return true;
	return false;
    }
   
}
