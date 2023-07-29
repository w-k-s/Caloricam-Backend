package com.wks.calorieapp.services.fatsecret.entities;


public class FSError extends FSAbstractResponse{

    private long code;
    private String message;
    
   
    public FSError(long code,String message)
    {
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
       return "[code: "+code+", message:"+message+"\n]";
    }
}
