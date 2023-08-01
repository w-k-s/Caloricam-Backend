package com.wks.calorieapp.api.fatsecret;

import java.util.HashMap;
import java.util.Map.Entry;
import java.io.IOException;
import java.net.URL;

import com.wks.calorieapp.api.fatsecret.entities.Method;
import com.wks.calorieapp.api.fatsecret.entities.OAuthBase;
import com.wks.calorieapp.api.fatsecret.entities.Parameter;
import com.wks.calorieapp.api.fatsecret.entities.Result;
import com.wks.calorieapp.utils.HttpClient;

public class FatSecretAPI
{

    private static final String URL = "http://platform.fatsecret.com/rest/server.api?";

    private static String consumerKey;
    private static String consumerSecret;;

    public FatSecretAPI(String consumerKey, String sharedKey)
    {
	if (consumerKey == null || sharedKey == null)
	    throw new IllegalStateException("Consumer Key and Shared Key values must not be null");

	FatSecretAPI.consumerKey = consumerKey;
	FatSecretAPI.consumerSecret = sharedKey;
    }

    private String doRestCall(Method method, HashMap<Parameter, String> methodParameters, Format responseFormat)
	    throws IOException
    {
	HashMap<String, String> requestParameters = new HashMap<String, String>();
	// add method name to parameters
	requestParameters.put(Parameter.METHOD.getName(), method.getName());
	// add response type to parameters
	requestParameters.put(Parameter.FORMAT.getName(), responseFormat.value);

	// add method parameters to request parameters
	for (Entry<Parameter, String> e : methodParameters.entrySet())
	    requestParameters.put(e.getKey().getName(), e.getValue());

	// append methodParameters and request parameters to url
	String urlWithParameters = addParametersToUrl(URL, requestParameters);

	// create Result object which will store URL with oauth signature
	Result result = new Result();

	// generate OAuth Signature
	OAuthBase oauthenticator = new OAuthBase();
	URL url = new URL(urlWithParameters);
	oauthenticator.generateSignature("GET", url, consumerKey, consumerSecret, null, null, result);

	// make rest call
	String signedUrl = result.getURL();
	return HttpClient.get(signedUrl);
    }

    // Convenience method for doing a REST call to the FatSecret Search method.
    public String foodsSearch(String searchExpression) throws IOException
    {

	HashMap<Parameter, String> parameters = new HashMap<Parameter, String>();
	parameters.put(Parameter.SEARCH_EXPRESSION, searchExpression);

	String result = doRestCall(Method.FOODS_SEARCH, parameters, Format.JSON);
	return result;
    }

    private String addParametersToUrl(String url, HashMap<String, String> parameters)
    {
	String params = "";
	for (Entry<String, String> entry : parameters.entrySet())
	{
	    params += "&" + entry.getKey() + "=" + entry.getValue();
	}

	return url + params.substring(1);
    }

    public enum Format
    {
	XML("xml"), JSON("json");

	private final String value;

	private Format(String value)
	{
	    this.value = value;
	}

	public String value()
	{
	    return this.value;
	}
    }
}