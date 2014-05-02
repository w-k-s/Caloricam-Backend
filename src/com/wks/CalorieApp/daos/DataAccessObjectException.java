package com.wks.calorieapp.daos;

/**
 * CITATION
 * 
 * Bauke Scholtz (July 7, 2008), DAO Tutorial - The Data Layer,
 * http://balusc.blogspot.ae/2008/07/dao-tutorial-data-layer.html
 * (retrieved on 11-July-2013)
 *
 */
public class DataAccessObjectException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 2765458971937680247L;

    public DataAccessObjectException(String message)
    {
	super(message);
    }
    
    public DataAccessObjectException(Throwable cause)
    {
	super(cause);
    }
    
    public DataAccessObjectException(String message, Throwable cause)
    {
	super(message,cause);
    }
}