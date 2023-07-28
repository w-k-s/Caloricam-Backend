package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.UserDao;
import com.wks.calorieapp.entities.User;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JSP_LOGIN = "/WEB-INF/login.jsp";
    private static final String SRVLT_ADMIN = "/admin";
    private static Logger logger = Logger.getLogger(AdminLogin.class);

    @Inject
    private UserDao userDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
        login.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final HttpSession session = req.getSession();
        final Boolean authenticated = (Boolean) req.getSession().getAttribute(Attributes.AUTHENTICATED.toString());
        final String username = req.getParameter(ContextParameters.USERNAME.toString());
        final String password = req.getParameter(ContextParameters.PASSWORD.toString());

        // check if user is already signed in
        if (Boolean.TRUE.equals(authenticated)) {
            logger.info(username + "  has resumed session.");
            resp.sendRedirect(req.getContextPath() + SRVLT_ADMIN);
            return;
        }

        if (username == null || password == null) {
            req.removeAttribute(Attributes.STATUS.toString());
            RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
            login.forward(req, resp);
            return;
        }

        // if username and password submitted, validate
        try {
            StatusCode loginStatus = loginCredentialsAreValid(username, password);

            switch (loginStatus) {
                case OK:
                    logger.info(username + " has signed in.");
                    session.setAttribute(Attributes.AUTHENTICATED.toString(), true);
                    session.setAttribute(Attributes.USERNAME.toString(), username);
                    resp.sendRedirect(req.getContextPath() + SRVLT_ADMIN);
                    return;

                default:
                    logger.info(username + " - " + loginStatus.getDescription());
                    req.setAttribute(Attributes.STATUS.toString(), loginStatus.getDescription());
                    RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
                    login.forward(req, resp);
            }
        } catch (DataAccessObjectException e) {
            logger.error("Login. DAOException encountered for user: " + username, e);
        }
    }

    private StatusCode loginCredentialsAreValid(String username, String password) throws DataAccessObjectException {
        final User user = userDAO.find(username);
        if (user == null) return StatusCode.NOT_REGISTERED;
        if (user.getPassword().equals(password)) return StatusCode.OK;
        return StatusCode.AUTHENTICATION_FAILED;
    }
}
