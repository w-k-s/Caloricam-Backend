package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.GeneralDAO;
import com.wks.calorieapp.utils.DatabaseUtils;

public class Admin extends HttpServlet
{
    // private static final boolean appIsDeployed = false;
    private static final long serialVersionUID = 1L;
    private static final String SRVLT_LOGIN = "/login";
    private static final String JSP_ADMIN = "/WEB-INF/admin.jsp";
    private static final String REDIRECT = "calorieapp";
    private static Logger logger = Logger.getLogger(Admin.class);
    // TODO remove later:
    private static Connection connection = null;

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtils.getConnection();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	// get params and attributes
	boolean authenticated = false;
	String query = req.getParameter("query");

	// load authentication variable.
	HttpSession session = req.getSession();
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    if (b != null) authenticated = b;
	}

	// check that the user is signed in.
	if (authenticated)
	{
	    //if the user has performed a query, do the query
	    if (query != null && !query.isEmpty())
	    {
		try
		{
		    logger.info("Executing query: " + query);
		    GeneralDAO gdao = new GeneralDAO(connection);
		    boolean isOk = gdao.doQuery(query);
		    req.setAttribute("query", isOk);

		} catch (DataAccessObjectException e)
		{
		    logger.error("Error while performing query: " + query, e);
		}
	    }

	    //load admin page.
	    RequestDispatcher admin = req.getRequestDispatcher(JSP_ADMIN);
	    admin.forward(req, resp);

	} else
	{
	    // if user is not signed in, redirect to login panel.
	    resp.sendRedirect(REDIRECT + SRVLT_LOGIN);

	}

	return;
    }

    // TODO remove later.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	doGet(req,resp);
    }

}
