package com.wks.CalorieApp.api.fatsecret;

import java.util.HashMap;
import java.util.Map.Entry;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.wks.CalorieApp.Utils.HTTPClient;





public class FatSecretAPI {
    
    private static final String URL = "http://platform.fatsecret.com/rest/server.api?";
    
    private static String consumerKey = "ea0d6a946b3e4b3a8d5cbdb0a55900dd";
    private static String consumerSecret = "49704a68e7114143925f6390aeca8b42";
    private Format responseFormat = Format.JSON;
   
	    
    public FatSecretAPI(String consumerKey, String sharedKey)
    {
	if(consumerKey == null || sharedKey == null)
	    throw new IllegalStateException("Consumer Key and Shared Key values must not be null");
	
	FatSecretAPI.consumerKey = consumerKey; 
	FatSecretAPI.consumerSecret = sharedKey;
    }
    
    public Format getResponseFormat() {
	return responseFormat;
    }
    
    public void setResponseFormat(Format responseFormat) {
	this.responseFormat = responseFormat;
    }
   
    //TODO make a more general method rather than this concrete one.
    public String foodsSearch(String searchExpression) throws IOException 
    {
	
	//add parameters to url
	HashMap<String,String> parameters = new HashMap<String,String>();
	parameters.put(Parameter.METHOD.getName(), Method.FOODS_SEARCH.getName());
	parameters.put(Parameter.FORMAT.getName(), getResponseFormat().value());
	parameters.put(Parameter.SEARCH_EXPRESSION.getName(), searchExpression);
	String urlWithParameters = addParametersToUrl(URL,parameters);
	
	//create result object to store url with Oauth signature 
	Result result = new Result();
	
	//generate oauth_signature
	OAuthBase oauth = new OAuthBase();
	URL url = null;
	try {
	    url = new URL(urlWithParameters);
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	
	oauth.generateSignature("GET",url, consumerKey, consumerSecret, null, null, result);
    	
	String signedUrl = result.getURL();
	
	String jsonResults = HTTPClient.get(signedUrl);
	return jsonResults;
	
    }
    
    private String addParametersToUrl(String url,HashMap<String,String> parameters)
    {
	String params = "";
	for(Entry<String, String> entry : parameters.entrySet())
	{
	    params += "&"+entry.getKey()+"="+entry.getValue();
	}
	
	return url+params.substring(1);
    }
    
    public enum Format{
	XML("xml"),
	JSON("json");
	
	private final String value;
	
	private Format(String value){
	    this.value = value;
	}
	
	public String value()
	{
	    return this.value;
	}
    }
}
