<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*,java.io.File"%>
<%!private static final String HREF_VIEW = "images?action=view&img=";
	private static final String HREF_DELETE = "images?action=delete&img=";%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin | Error</title>
</head>
<body>
<h1>Image Error Page</h1>
<p>${error}</p>
</body>
</html>