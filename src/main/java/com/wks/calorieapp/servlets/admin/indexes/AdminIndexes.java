package com.wks.calorieapp.servlets.admin.indexes;

import com.wks.calorieapp.servlets.admin.ResponseDecorator;
import com.wks.calorieapp.services.IndexingService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.wks.calorieapp.servlets.admin.ResponseDecorator.View.INDEXES;

public class AdminIndexes extends HttpServlet {

    private static final long serialVersionUID = 4863024199042688296L;
    private final static String ACTION_DELETE = "delete";
    private final static String ACTION_REINDEX = "reindex";

    private static Logger logger = Logger.getLogger(AdminIndexes.class);

    @Inject
    private IndexingService indexer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminIndexesRequestDecorator adminIndexesRequest = AdminIndexesRequestDecorator.of(req);
        final String action = adminIndexesRequest.getAction();

        if (action != null) {
            boolean success = handleAction(action);
            logger.info("Admin Index. action='" + action + "', success='" + success + "'");
        }

        adminIndexesRequest.setIndexFilesList(indexer.getIndexFilesList());
        ResponseDecorator.of(req, resp).forwardTo(INDEXES);
    }

    private boolean handleAction(String action) {
        logger.info("Admin Index. action = '" + action + "'.");
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
            logger.error("Admin index. Unidentified action: " + action);
            return false;
        }
    }
}
