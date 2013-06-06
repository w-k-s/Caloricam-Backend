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

public class AdminLogin extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    private static final String JSP_LOGIN = "/WEB-INF/login.jsp";

    // Following a strict MVC pattern i.e. one servlet for each jsp
    // only the admin servlet is allowed to load the admin.jsp
    // so this servlet will redirect to admin servlet instead of loading the
    // admin.jsp.
    private static final String SRVLT_ADMIN = "/admin";

    // private static final String REDIRECT = "/calorieapp";

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
	    RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
	    admin.forward(req, resp);
	    return;
	}

	// if username and password submitted, validate
	if (username != null && password != null)
	{
	    if (loginCredentialsAreValid(username, password))
	    {

		session.setAttribute(Attribute.AUTHENTICATED.toString(), true);
		session.setAttribute(Attribute.USERNAME.toString(), username);

		RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
		admin.forward(req, resp);
		return;
	    } else
	    {
		req.setAttribute(Attribute.STATUS.toString(), Status.INCORRECT_USERNAME_PASSWORD.getMessage());
		RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
		login.forward(req, resp);
		return;
	    }
	} else
	{
	    req.removeAttribute(Attribute.STATUS.toString());
	    RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
	    login.forward(req, resp);
	    return;
	}

    }

    private boolean loginCredentialsAreValid(String username, String password)
    {
	UserDataAccessObject usersDb = new UserDataAccessObject(getServletContext());
	User user = null;

	user = usersDb.find(username);

	if (user == null) return false;

	if (user.getPassword().equals(password)) return true;
	return false;
    }

    enum Status
    {
	NULL_USERNAME_PASSWORD("Must provide a valid username and password."), INCORRECT_USERNAME_PASSWORD(
		"Username or password is not correct."), NOT_REGISTERED("This username is not registered as admin.");

	private final String message;

	Status(String message)
	{
	    this.message = message;
	}

	public String getMessage()
	{
	    return message;
	}
    }
}
