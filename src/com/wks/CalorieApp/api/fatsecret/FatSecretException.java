package com.wks.calorieapp.api.fatsecret;

public class FatSecretException extends Exception{

    private static final long serialVersionUID = -3541442839619127733L;
    private long code;
    private String message;
    
    public FatSecretException(String message)
    {
	super(message);
	this.code = -1;
	this.message = message;
    }
    
    public FatSecretException(long code,String message)
    {
	super(code+" - "+message);
	this.code = code;
	this.message = message;
    }
    
    public long getCode() {
	return code;
    }
    
    public String getMessage() {
	return message;
    }
    
    @Override
    public String toString() {
       return ""+code+" - "+message;
    }
}
