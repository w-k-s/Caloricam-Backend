package api.fatsecret.platform.Models;

public class FatSecretException extends Exception{
    private int code;
    private String message;
    
    public FatSecretException(String message)
    {
	super(message);
	this.code = -1;
	this.message = message;
    }
    
    public FatSecretException(int code,String message)
    {
	super(code+" - "+message);
	this.code = code;
	this.message = message;
    }
    
    public int getCode() {
	return code;
    }
    
    public String getMessage() {
	return message;
    }
}
