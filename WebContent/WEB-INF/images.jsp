<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,java.io.File"%>
<%!
	private static final String HREF_VIEW = "images?action=view&img=";
	private static final String HREF_DELETE = "images?action=delete&img=";
	private static final String ATTR_IMAGE_LIST = "images";
	private static final String ATTR_IMAGE_DIR = "image_dir";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	String imageDir = (String) request.getAttribute(ATTR_IMAGE_DIR);
	List<String> files = (ArrayList<String>) request.getAttribute(ATTR_IMAGE_LIST);
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
	    File f = new File(imageDir+file);
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