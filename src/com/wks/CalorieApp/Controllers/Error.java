package com.wks.CalorieApp.Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wks.CalorieApp.Models.Response;

public class Error extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 2893179894559140866L;
    private static final String CONTENT_TYPE = "application/json";
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
       resp.setContentType(CONTENT_TYPE);
       PrintWriter out = resp.getWriter();
       
       Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
       String s = System.getenv("$OPENSHIFT_MYSQL_DB_HOST")+"--"+System.getenv("$OPENSHIFT_MYSQL_DB_PORT");
       
       out.println( new Response(false,s+throwable.toString()).toJSON() );
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        doGet(req,resp);
    }
}
