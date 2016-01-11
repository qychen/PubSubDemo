localStorage.clear();
sessionStorage.clear();
//mode = 0 when use as login page, = 1 use as new user page
var mode = 0;
var BaseURL = "http://localhost:8080/PubSubDemo/";

function Login(){
  if (mode == 0){
	var UserName = document.getElementById("username");
	var UserNameStr = UserName.value;
	if (UserNameStr != ""){
        //basic validation of input
        re = /^\w+$/;
        if(!re.test(UserNameStr)) {
            alert("Error: Username must contain only letters, numbers and underscores!");
            UserName.focus();
        }
        else{
            //sending request to UserServlet
            var URL = BaseURL + "/UserServlet";
            URL += "?username=" + UserNameStr;

            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
		        if (xhr.readyState == 4 && xhr.status == 200) {
		          //response
		          response = this.responseText.trim();
		          //console.log(response);
		          result = response.split("\n")[0];
		          if (result == "yes")
		          {
		        	  var responsejson = JSON.parse(response.split("\n")[1]);
		        	  localStorage.setItem("sub", JSON.stringify(responsejson["sub"]));
		        	  localStorage.setItem("pub", JSON.stringify(responsejson["pub"]));
		        	  localStorage.setItem("username", UserNameStr);
		        	  window.location.assign(BaseURL + "home.html");
		          }
		          else
		              alert("The Username is incorrect!");
		        }
            }
            xhr.open("GET", URL, true);
            xhr.send();
        }
	}
	else
		alert("The Username cannot be empty!");
  }
  else{
	  mode = 0;
	  var show = document.getElementById("label");
	  show.innerHTML = "Please input your Username to login:";
	  
  }
}

function NewUser(){
    if (mode == 0){
    	mode = 1;
		var show = document.getElementById("label");
		show.innerHTML = "Please input your Username to create a new account:";
    }
    else{
    	var UserName = document.getElementById("username");
    	var UserNameStr = UserName.value;
    	if (UserNameStr != ""){
            re = /^\w+$/;
            if(!re.test(UserNameStr)) {
                alert("Error: Username must contain only letters, numbers and underscores!");
                UserName.focus();
            }
            else{
                //sending request to UserServlet
                var URL = BaseURL + "/UserServlet";
                URL += "?username=" + UserNameStr;
                URL += "&flag=1";

                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function() {
    		        if (xhr.readyState == 4 && xhr.status == 200) {
    		          //response
    		          response = this.responseText.trim();
    		          result = response.split("\n")[0];
    		          if (result == "yes")
    		          {
    		              alert("Successfully create new user as "+UserNameStr+"!");
    		        	  window.location.replace(BaseURL + "index.html");
    		              
    		          }
    		          else
    		              alert("The Username already exists!");
    		        }
                }
                xhr.open("GET", URL, true);
                xhr.send();
            }
    		
    	}
    	else
    		alert("The Username cannot be empty!");
    }
}







