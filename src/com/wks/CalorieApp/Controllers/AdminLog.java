package com.wks.calorieapp.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wks.calorieapp.utils.Environment;

public class AdminLog extends HttpServlet
{

    private static final long serialVersionUID = 7511744497191175512L;
    private static final String CONTENT_TYPE = "text/plain";
    private static final String REDIRECT = "/calorieapp";
    private static final String SRVLT_LOGIN = "/login";
    private static Object lock = new Object();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	boolean authenticated = authenticate(req.getSession());

	resp.setContentType(CONTENT_TYPE);
	PrintWriter out = resp.getWriter();

	if (!authenticated)
	{
	    // redirect to login page
	    resp.sendRedirect(REDIRECT + SRVLT_LOGIN);
	    return;
	}

	String log = readLog();
	out.println(log);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

	doGet(req, resp);
    }

    private boolean authenticate(HttpSession session)
    {
	boolean authenticated = false;
	synchronized (session)
	{
	    Boolean b = (Boolean) session.getAttribute(Attribute.AUTHENTICATED.toString());
	    if (b != null) authenticated = b;
	}
	return authenticated;
    }

    private String readLog() throws IOException
    {
	synchronized (lock)
	{
	    String logUri = Environment.getLogFile(getServletContext());

	    File logFile = new File(logUri);
	    if (!logFile.exists()) return Status.FILE_NOT_FOUND.getMessage();

	    // Log file exists, read it.
	    BufferedReader reader = null;
	    String logText = "Log:\n";
	    try
	    {
		reader = new BufferedReader(new FileReader(logFile));
		String line = "";
		while ((line = reader.readLine()) != null)
		{
		    logText += line + "\n";

		}
	    } catch (FileNotFoundException e)
	    {
		e.printStackTrace();
		return Status.FILE_NOT_FOUND.getMessage() + ": " + e;
	    } catch (IOException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return Status.IO_ERROR.getMessage() + ": " + e;
	    } finally
	    {
		try
		{
		    if (reader != null) reader.close();
		} catch (IOException e)
		{
		    return Status.IO_ERROR.getMessage() + ": " + e;
		}
	    }
	    return logText;
	}

    }

    enum Status
    {
	FILE_NOT_FOUND("No log file found. "), IO_ERROR("An IOException occured while reading log file.");

	private final String message;

	Status(String message)
	{
	    this.message = message;
	}

	public String getMessage()
	{
	    return message;
	}
    }
}
