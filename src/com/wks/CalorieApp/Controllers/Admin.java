package com.wks.CalorieApp.Controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Admin extends HttpServlet{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String ATTR_AUTHENTICATED = "authenticated";
    private static final String SRVLT_LOGIN = "/login";
    private static final String JSP_ADMIN = "/WEB-INF/admin.jsp";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
	//get params and attributes
	boolean authenticated = false;
	HttpSession session = req.getSession();
	synchronized(session)
	{
	    Boolean b = (Boolean) session.getAttribute(ATTR_AUTHENTICATED);
	    if(b != null) authenticated = b;
	}
	
	
	//check that the user is signed in.
	if(authenticated)
	{
	    //if user is signed in, load admin
	    RequestDispatcher admin = req.getRequestDispatcher(JSP_ADMIN);
	    admin.forward(req, resp);
	    return;
	}else{
	    //if user is not signed in, redirect to login panel.
	    RequestDispatcher login = req.getRequestDispatcher(SRVLT_LOGIN);
	    login.forward(req, resp);
	    return;
	}
    }
}
