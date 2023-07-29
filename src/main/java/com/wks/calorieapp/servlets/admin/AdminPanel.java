package com.wks.calorieapp.servlets.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.wks.calorieapp.servlets.admin.ResponseDecorator.View.ADMIN;

public class AdminPanel extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDecorator.of(req, resp).forwardTo(ADMIN);
    }
}
