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
import com.wks.calorieapp.daos.UserDAO;
import com.wks.calorieapp.entities.StatusCode;
import com.wks.calorieapp.entities.User;
import com.wks.calorieapp.utils.DatabaseUtils;

public class AdminLogin extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    private static final String JSP_LOGIN = "/WEB-INF/login.jsp";

    // Following a strict MVC pattern i.e. one servlet for each jsp
    // only the admin servlet is allowed to load the admin.jsp
    // so this servlet will redirect to admin servlet instead of loading the
    // admin.jsp.
    private static final String SRVLT_ADMIN = "/admin";
    private static Logger logger = Logger.getLogger(AdminLogin.class);
    private static Connection connection = null;

    @Override
    public void init() throws ServletException
    {
	connection = DatabaseUtils.getConnection();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	// TODO Auto-generated method stub
	doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	// get all parameters
	boolean authenticated = false;
	String username = null;
	String password = null;

	HttpSession session = req.getSession();
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    if (b != null) authenticated = b;
	}

	username = req.getParameter(Parameter.USERNAME.toString());
	password = req.getParameter(Parameter.PASSWORD.toString());

	// check if user is already signed in
	if (authenticated)
	{
	    logger.info(username + "  has resumed session.");
	    RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
	    admin.forward(req, resp);
	    return;
	}

	// if username and password submitted, validate
	if (username != null && password != null)
	{
	    StatusCode loginStatus;
	    try
	    {
		loginStatus = loginCredentialsAreValid(username, password);

		switch (loginStatus)
		{
		case OK:
		    logger.info(username + " has signed in.");
		    session.setAttribute(Attribute.AUTHENTICATED.toString(), true);
		    session.setAttribute(Attribute.USERNAME.toString(), username);

		    RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
		    admin.forward(req, resp);
		    return;

		default:
		    logger.info(username + " - " + loginStatus.getDescription());
		    req.setAttribute(Attribute.STATUS.toString(), loginStatus.getDescription());
		    RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
		    login.forward(req, resp);
		    return;
		}
	    } catch (DataAccessObjectException e)
	    {
		logger.error("Login. DAOException encountered for user: "+username,e);
	    }

	} else
	{
	    req.removeAttribute(Attribute.STATUS.toString());
	    RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
	    login.forward(req, resp);
	    return;
	}

    }

    private StatusCode loginCredentialsAreValid(String username, String password) throws DataAccessObjectException
    {
	if (connection == null) return StatusCode.DB_NULL_CONNECTION;

	UserDAO usersDb = new UserDAO(connection);
	User user = null;

	user = usersDb.find(username);

	if (user == null) return StatusCode.NOT_REGISTERED;
	if (user.getPassword().equals(password)) return StatusCode.OK;
	return StatusCode.AUTHENTICATION_FAILED;
    }


}
