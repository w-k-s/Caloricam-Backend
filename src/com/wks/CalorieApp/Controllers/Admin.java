package com.wks.calorieapp.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Admin extends HttpServlet
{
    //private static final boolean appIsDeployed = false;
    private static final long serialVersionUID = 1L;
    private static final String SRVLT_LOGIN = "/login";
    private static final String JSP_ADMIN = "/WEB-INF/admin.jsp";
    private static final String REDIRECT = "calorieapp";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	// get params and attributes
	boolean authenticated = false;

	HttpSession session = req.getSession();

	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    if (b != null) authenticated = b;
	}

	// check that the user is signed in.
	if (authenticated)
	{
	    // if user is signed in, load admin
	    RequestDispatcher admin = req.getRequestDispatcher(JSP_ADMIN);
	    admin.forward(req, resp);

	} else
	{
	    // if user is not signed in, redirect to login panel.
	    resp.sendRedirect(REDIRECT + SRVLT_LOGIN);

	}

	return;
    }
}
