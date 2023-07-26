function doValidation()
{
	username = document.getElementById("username").value;
	password = document.getElementById("password").value;
	
	if(username.length==0 || password.length==0){
		alert("Must provide a username and password.");
		return false;
	}
	
	return true;
}