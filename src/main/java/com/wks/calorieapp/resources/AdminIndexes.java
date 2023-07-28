package com.wks.calorieapp.resources;

import com.wks.calorieapp.services.IndexingService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AdminIndexes extends HttpServlet {

    private static final long serialVersionUID = 4863024199042688296L;
    private final static String ACTION_DELETE = "delete";
    private final static String ACTION_REINDEX = "reindex";

    private final static String JSP_INDEXES = "/WEB-INF/indexes.jsp";
    private final static String SRVLT_LOGIN = "/login";

    private static Logger logger = Logger.getLogger(AdminIndexes.class);

    @Inject
    private IndexingService indexer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean authenticated = false;
        String username = "";
        String action = null;

        HttpSession session = req.getSession();
        synchronized (session) {
            Boolean b = (Boolean) session.getAttribute(Attributes.AUTHENTICATED.toString());
            username = (String) session.getAttribute(Attributes.USERNAME.toString());
            if (b != null) authenticated = b;
        }

        if (!authenticated) {
            logger.info("Admin Index. Page requested. User not authenticated");
            resp.sendRedirect(req.getContextPath() + SRVLT_LOGIN);
            return;
        } else {
            logger.info("Admin Index. Page requested by " + username);
        }

        action = req.getParameter(ContextParameters.ACTION.toString());
        logger.info("Admin Index. action = '" + action + "'.");
        if (action != null) {
            boolean success = handleAction(action);
            logger.info("Admin Index. action='" + action + "', success='" + success + "'");
        }


        req.setAttribute(Attributes.INDEX_LIST.toString(), indexer.getIndexFilesList());
        RequestDispatcher indexView = req.getRequestDispatcher(JSP_INDEXES);
        indexView.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private boolean handleAction(String action) {
        if (action.equalsIgnoreCase(ACTION_DELETE)) {
            return indexer.deleteIndexes();
        } else if (action.equalsIgnoreCase(ACTION_REINDEX)) {
            try {
                return indexer.reindex();
            } catch (FileNotFoundException e) {
                logger.error("Admin Index. FileNotFoundException encountered while reindexing.", e);
                e.printStackTrace();
            } catch (IOException e) {
                logger.error("Admin Index. IOException encountered while reindexing.", e);
                e.printStackTrace();
            }
            return false;
        } else {
            logger.error("Admin index. Unidentified action from " + JSP_INDEXES + ": " + action);
            return false;
        }
    }
}
