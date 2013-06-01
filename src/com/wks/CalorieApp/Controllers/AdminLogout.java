package com.wks.CalorieApp.Controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminLogout extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String ATTR_AUTHENTICATED = "authenticated";
    private static final String SRVLT_LOGIN = "/login";
    private static final String REDIRECT = "/calorieapp";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
	HttpSession session = req.getSession();
	synchronized(session)
	{
	    if(session.getAttribute(ATTR_AUTHENTICATED) != null)
		session.invalidate();
	    
	}
	resp.sendRedirect(REDIRECT+SRVLT_LOGIN);
	return;
    }
    
    
    
}
