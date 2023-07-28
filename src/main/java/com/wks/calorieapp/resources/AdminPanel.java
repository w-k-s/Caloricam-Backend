package com.wks.calorieapp.resources;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminPanel extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JSP_ADMIN = "/WEB-INF/admin.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //load admin page.
        RequestDispatcher admin = req.getRequestDispatcher(JSP_ADMIN);
        admin.forward(req, resp);
    }

}
