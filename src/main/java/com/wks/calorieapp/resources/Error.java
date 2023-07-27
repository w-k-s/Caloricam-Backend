package com.wks.calorieapp.resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class Error extends HttpServlet
{

    private static final long serialVersionUID = 2893179894559140866L;
    private static final String CONTENT_TYPE = "application/json";
    private static Logger logger = Logger.getLogger(Error.class);
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
       resp.setContentType(CONTENT_TYPE);
       PrintWriter out = resp.getWriter();
       
       //log stack trace
       Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
       StringWriter sw = new StringWriter();
       PrintWriter pw = new PrintWriter(sw);
       throwable.printStackTrace(pw);
       logger.fatal(sw.toString());
       
       //display response.
       Response response = new Response(StatusCode.SERVICE_FAILED, sw.toString());
       out.println( response.toJSON() );
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        doGet(req,resp);
    }
}
