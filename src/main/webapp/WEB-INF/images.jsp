<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,java.io.File,com.wks.calorieapp.resources.Attributes"%>
<%!private static final String HREF_VIEW = "images?action=view&img=";
	private static final String HREF_DELETE = "images?action=delete&img=";%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin | Images</title>
</head>
<body>
<h1>Image Admin Page</h1>
<p>Click on view to see the picture.</p>
<p>Click on delete to delete an image; corresponding database record will also be deleted.</p>

<%
	List<String> files = (ArrayList<String>) request.getAttribute(Attributes.IMAGE_LIST.toString());
%>
<table>
	<tr>
		<th>file name</th>
		<th>file size</th>
		<th>view</th>
		<th>delete</th>
	</tr>
<%
	for(String file : files)
	{
	    File f = new File(file);
	    String view=HREF_VIEW+file;
	    String delete=HREF_DELETE+file;
	    
	    out.println("<tr>");
	    out.println("<td>"+file+"</td>");
	    out.println("<td>"+f.length()+"</td>");
	    out.println("<td><a href='"+view+"'>VIEW</a></td>");
	    out.println("<td><a href='"+delete+"'>DELETE</a></td>");
	    
	    out.println("</tr>");
	}
%>
</table>
</body>
</html>