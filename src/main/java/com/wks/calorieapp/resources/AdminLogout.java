package com.wks.calorieapp.resources;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class AdminLogout extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String SRVLT_LOGIN = "/login";
    private static Logger logger = Logger.getLogger(AdminLogout.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();

        if (session.getAttribute(Attributes.AUTHENTICATED.toString()) != null) {
            logger.info((String) session.getAttribute(Attributes.USERNAME.toString()) + " has logged out.");
        }
        session.invalidate();
        resp.sendRedirect(req.getContextPath() + SRVLT_LOGIN);
        return;
    }

}
