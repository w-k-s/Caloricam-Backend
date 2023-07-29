package com.wks.calorieapp.servlets.admin.login;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.servlets.admin.ResponseDecorator;
import com.wks.calorieapp.services.ServiceException;
import com.wks.calorieapp.services.UserService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.wks.calorieapp.servlets.admin.ResponseDecorator.Path.ADMIN;
import static com.wks.calorieapp.servlets.admin.ResponseDecorator.View.LOGIN;

public class AdminLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(AdminLogin.class);

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDecorator responseDecorator = ResponseDecorator.of(req, resp);
        responseDecorator.forwardTo(LOGIN);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final LoginSessionDecorator loginSessionDecorator = LoginSessionDecorator.of(req.getSession());
        final LoginRequestDecorator loginRequestDecorator = LoginRequestDecorator.of(req);
        final ResponseDecorator responseDecorator = ResponseDecorator.of(req, resp);
        final String username = loginRequestDecorator.getUsername();
        final String password = loginRequestDecorator.getPassword();

        // check if user is already signed in
        if (loginSessionDecorator.isAuthenticated()) {
            logger.info(loginRequestDecorator.getUsername() + "  has resumed session.");
            responseDecorator.redirectTo(ADMIN);
            return;
        }

        if (username == null || password == null) {
            logger.info(loginRequestDecorator.getUsername() + "Redirecting to login page.");
            loginRequestDecorator.removeFlashMessage();
            responseDecorator.forwardTo(LOGIN);
            return;
        }

        try {
            userService.authenticate(username, password);
            logger.info(username + " has signed in.");
            loginSessionDecorator
                    .setUsername(username)
                    .setAuthenticated();
            responseDecorator.redirectTo(ADMIN);
        } catch (ServiceException | DataAccessObjectException e) {
            logger.info(username + " - " + e.getMessage());
            loginRequestDecorator.setFlashMessage(e.getMessage());
            responseDecorator.forwardTo(LOGIN);
        }
    }
}
