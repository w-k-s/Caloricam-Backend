package com.wks.calorieapp.resources;

import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminPanel extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String SRVLT_LOGIN = "/login";
    private static final String JSP_ADMIN = "/WEB-INF/admin.jsp";
    private static Logger logger = Logger.getLogger(AdminPanel.class);
    // TODO remove later:


    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get params and attributes
        boolean authenticated = false;

        // load authentication variable.
        HttpSession session = req.getSession();
        synchronized (session) {
            Boolean b = (Boolean) session.getAttribute(Attributes.AUTHENTICATED.toString());
            if (b != null) authenticated = b;
        }

        // check that the user is signed in.
        if (authenticated) {

            //load admin page.
            RequestDispatcher admin = req.getRequestDispatcher(JSP_ADMIN);
            admin.forward(req, resp);

        } else {
            // if user is not signed in, redirect to login panel.
            resp.sendRedirect(req.getContextPath() + SRVLT_LOGIN);

        }

        return;
    }

    // TODO remove later.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
