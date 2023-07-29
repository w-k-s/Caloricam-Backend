package com.wks.calorieapp.servlets.admin.login;

import com.wks.calorieapp.servlets.admin.ResponseDecorator;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.wks.calorieapp.servlets.admin.ResponseDecorator.Path.LOGIN;

public class AdminLogout extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminLogout.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginSessionDecorator loginSession = LoginSessionDecorator.of(req.getSession());
        if (loginSession.isAuthenticated()) {
            LOGGER.info(loginSession.getUsername() + " has logged out.");
        }
        req.getSession().invalidate();
        ResponseDecorator.of(req, resp).redirectTo(LOGIN);
    }

}
