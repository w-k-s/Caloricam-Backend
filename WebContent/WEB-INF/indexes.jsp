<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,com.wks.calorieapp.controllers.Attribute, com.wks.calorieapp.utils.Environment, java.io.File"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin | Indexes</title>
</head>
<body>
<h1>Indexes Admin Page</h1>
<hr/>
<h2>Tools</h2>
<ul>
	<!-- <li><a href="indexes?action=delete">Delete all</a></li>
	<li><a href="indexes?action=reindex">Reindex all</a></li> -->
</ul>
<strong><em>Note: Must reindex images after deleting them.</em></strong>
<hr/>
<%
	List<String> indexFiles = (ArrayList<String>) request.getAttribute(Attribute.INDEX_LIST.toString());
%>
<table>
	<tr>
		<th>File name</th>
		<th>File size</th>
	</tr>
	<%
	
	String indexesDir = Environment.getIndexesDirectory(application);
	
	for(String fileName : indexFiles)
	{
	    File indexFile = new File(indexesDir + fileName);
	    
	    out.println("<tr>");
	    out.println("<td>"+ fileName +"</td>");
	    out.println("<td>"+ indexFile.length() + "</td>");
	    out.println("</tr>");
	}	
	%>
</table>
</body>
</html>