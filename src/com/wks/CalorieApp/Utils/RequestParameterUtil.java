package com.wks.calorieapp.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestParameterUtil
{
    private static final String PARAMETER_SEPERATOR = "/";
    
    public static String[] getRequestParameters(HttpServletRequest request)
    {
	// check that parameters were provided
	if (request.getPathInfo() == null) return null;

	String[] _params = request.getPathInfo().split(PARAMETER_SEPERATOR);
	String[] params = new String[_params.length];
	for (int i = 0; i < _params.length; i++)
	    params[i] = _params[i];

	return params;	
    }
    
    public static boolean parametersMatchFormats(String[] parameters,String[] patterns)
    {
	if(parameters.length != patterns.length)
	    throw new IllegalArgumentException("The pattern array should contain a regex for each parameter in the parameters array");
	    
	int parametersMatched = 0;
	for(int i=0;i<parameters.length;i++)
	{
	    if(parameters[i].matches(patterns[i]))
		parametersMatched++;
	}
	
	return (parameters.length == parametersMatched);
    }
}
