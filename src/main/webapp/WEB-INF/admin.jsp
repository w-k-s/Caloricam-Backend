<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
    if (session.getAttribute("authenticated") != null
					&& session.getAttribute("authenticated").equals(false)) {
				response.sendRedirect("/calorieapp/logout");

			}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin Panel</title>
<style type="text/css">
td {
	vertical-align: top;
}
</style>
</head>
<body>
	<h1>ADMIN PANEL v. ${initParam.app_version} </h1>
	<hr />
	<h2>Navigation</h2>
	<ul>
		<li><a href="${pageContext.request.contextPath}/images">Images</a></li>
		<li><a href="${pageContext.request.contextPath}/indexes">Indexes</a></li>
		<li><a href="${pageContext.request.contextPath}/logout">Logout (${username})</a></li>
	</ul>
	<hr />
	<h2>Upload</h2>
	<form name="upload" method="post" action="api/upload" enctype="multipart/form-data">
		<input type="file" name="image" /><br /> <input type="submit" value="Upload" />
	</form>
</body>
</html>