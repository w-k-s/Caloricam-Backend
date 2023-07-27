package com.wks.calorieapp.resources;

import com.wks.calorieapp.daos.DataAccessObjectException;
import com.wks.calorieapp.daos.ImageDAO;
import com.wks.calorieapp.daos.UserDAO;
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

    // Following a strict MVC pattern i.e. one servlet for each jsp
    // only the admin servlet is allowed to load the admin.jsp
    // so this servlet will redirect to admin servlet instead of loading the
    // admin.jsp.
    private static final String SRVLT_ADMIN = "/admin";
    private static Logger logger = Logger.getLogger(AdminLogin.class);

    @Inject
    private ImageDAO imageDAO;

    @Inject
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get all parameters
        boolean authenticated = false;
        String username = null;
        String password = null;

        HttpSession session = req.getSession();
        synchronized (session) {
            Boolean b = (Boolean) session.getAttribute(Attributes.AUTHENTICATED.toString());
            if (b != null) authenticated = b;
        }

        username = req.getParameter(ContextParameters.USERNAME.toString());
        password = req.getParameter(ContextParameters.PASSWORD.toString());

        // check if user is already signed in
        if (authenticated) {
            logger.info(username + "  has resumed session.");
            RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
            admin.forward(req, resp);
            return;
        }

        // if username and password submitted, validate
        if (username != null && password != null) {
            StatusCode loginStatus;
            try {
                loginStatus = loginCredentialsAreValid(username, password);

                switch (loginStatus) {
                    case OK:
                        logger.info(username + " has signed in.");
                        session.setAttribute(Attributes.AUTHENTICATED.toString(), true);
                        session.setAttribute(Attributes.USERNAME.toString(), username);

                        RequestDispatcher admin = req.getRequestDispatcher(SRVLT_ADMIN);
                        admin.forward(req, resp);
                        return;

                    default:
                        logger.info(username + " - " + loginStatus.getDescription());
                        req.setAttribute(Attributes.STATUS.toString(), loginStatus.getDescription());
                        RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
                        login.forward(req, resp);
                        return;
                }
            } catch (DataAccessObjectException e) {
                logger.error("Login. DAOException encountered for user: " + username, e);
            }

        } else {
            req.removeAttribute(Attributes.STATUS.toString());
            RequestDispatcher login = req.getRequestDispatcher(JSP_LOGIN);
            login.forward(req, resp);
            return;
        }

    }

    private StatusCode loginCredentialsAreValid(String username, String password) throws DataAccessObjectException {
        User user = null;

        user = userDAO.find(username);

        if (user == null) return StatusCode.NOT_REGISTERED;
        if (user.getPassword().equals(password)) return StatusCode.OK;
        return StatusCode.AUTHENTICATION_FAILED;
    }


}
