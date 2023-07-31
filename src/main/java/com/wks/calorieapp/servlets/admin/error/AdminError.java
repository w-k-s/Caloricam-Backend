package com.wks.calorieapp.servlets.admin.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.wks.calorieapp.servlets.admin.ResponseDecorator;
import com.wks.calorieapp.servlets.admin.images.AdminImagesRequestDecorator;
import com.wks.calorieapp.utils.FileUtils;
import org.apache.log4j.Logger;

import static com.wks.calorieapp.servlets.admin.ResponseDecorator.View.ERROR;

public class AdminError extends HttpServlet {
    private static final long serialVersionUID = 2893179894559140866L;
    private static Logger logger = Logger.getLogger(AdminError.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");

        //log stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        logger.fatal(sw.toString());

        // --- Print error
        AdminErrorRequestDecorator.of(req).setMessage(sw.toString());
        ResponseDecorator.of(req, resp).forwardTo(ERROR);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
