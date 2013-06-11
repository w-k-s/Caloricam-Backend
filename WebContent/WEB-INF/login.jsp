<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin | Login</title>
</head>
<body>
<form name="login" method="post" action="login" onsubmit="return doValidation();">
<table>
	<tr>
		<td>Username:</td>
		<td><input type="text" id="username" name="username" size="20"/></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" id="password" name="password" size="20"/></td>
	</tr>
	<tr >
		<td colspan="2"><input type="submit" value="Login"/>
	</tr>
	<tr>
		<td colspan="2">${status}</td>
	</tr>
</table>
</form>
<script type="text/javascript" src="js/login.js"></script>
</body>
</html>