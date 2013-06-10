package com.wks.calorieapp.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.wks.calorieapp.models.Response;

public class Error extends HttpServlet
{

    /**
     * 
     */
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
       
       //get time
       Calendar cal = Calendar.getInstance();
       SimpleDateFormat format = new SimpleDateFormat("h:mm a dd/MM/yyyy");
       String time = format.format(cal.getTime());
       
       //display response.
       out.println( new Response(false,time+" - "+"Web Service failed. Please report this incident.").toJSON() );
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        doGet(req,resp);
    }
}
