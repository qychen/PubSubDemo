var BaseURL = "http://localhost:8080/PubSubDemo/";	

	document.getElementById("topic").innerHTML = sessionStorage.getItem("click_topic");
	if (sessionStorage.getItem("unreadMessage") === null){
		if (document.getElementById("no_message"))
			document.getElementById("no_message").innerHTML = "You currently do not have new message";
	}
	else{
		messages = sessionStorage.getItem("unreadMessage");
		var list = document.getElementById("meslist");
		var i = 0;
		for (i=0;i<messages.split("\n").length;i++){
			var entry = document.createElement('li');
			entry.appendChild(document.createTextNode(messages.split("\n")[i]));
			list.appendChild(entry);
		}
		sessionStorage.removeItem("unreadMessage");
	}
	
	var clicked = 0;
	
	function allMessages(){
		//get all message from TopicServlet
		var list = document.getElementById("meslist");
		//check if the request already sent
		if (clicked == 0){
			clicked = 1;
			while (list.hasChildNodes()) {   
			    list.removeChild(list.firstChild);
			}
	        var URL = BaseURL + "/TopicServlet";
	        URL += "?topic=" + sessionStorage.getItem("click_topic");

	        var xhr = new XMLHttpRequest();
	        xhr.onreadystatechange = function() {
		        if (xhr.readyState == 4 && xhr.status == 200) {
		          //response
		          response = this.responseText.trim();
		          console.log(response);
		  		  document.getElementById("title2").innerHTML = "All Messages";
		  		  document.getElementById("no_message").innerHTML = "";
			   	  var i = 0;
				  for (i=0;i<response.split("\n").length;i++){
					var entry = document.createElement('li');
					entry.appendChild(document.createTextNode(response.split("\n")[i]));
					list.appendChild(entry);
				  }

		        }
	        }
	        xhr.open("GET", URL, true);
	        xhr.send();
		}
	}

	function subtopic(){
		//update user data in UserServlet by POST
		var topicname = sessionStorage.getItem("click_topic");

        var URL = BaseURL + "/UserServlet";
        URL += "?topic=" + sessionStorage.getItem("click_topic");
        URL += "&username=" + localStorage.getItem("username");

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	          //response
	          response = this.responseText.trim();
	          console.log(response);
	          sessionStorage.setItem("unreadMessage", response);
	          
	          //update sub pub for home.html
	   		  var sub = JSON.parse(localStorage.getItem("sub"));
	   		  sub.push(JSON.parse("{\"name\":\""+topicname+"\" ,\"new\":\"0\"}"));
	  		  localStorage.setItem("sub", JSON.stringify(sub));

	   		  var pub = JSON.parse(localStorage.getItem("pub"));
	  		  for (i=0;i<pub.length;i++){
	  			if (pub[i]["name"] == topicname){
	  				pub.splice(i, 1);
	  				break;
	  			}
	  		  }
	  		  localStorage.setItem("pub", JSON.stringify(pub));
	  		  
	  		  alert("Successfully subscribed topic:" + topicname);
	          //redirect to subtopic
	          //replace will not leaving this page in history stack, so when call backpage()
	          //in subtopic.html, it will directly return to home.html rather than pubtopic.html
	    	  window.location.replace(BaseURL + "subtopic.html");
	        }
        }
        xhr.open("POST", URL, true);
        xhr.send();
		
	}

	function unsubtopic(){
		//update user data in UserServlet by PUT
		var topicname = sessionStorage.getItem("click_topic");

        var URL = BaseURL + "/UserServlet";
        URL += "?topic=" + sessionStorage.getItem("click_topic");
        URL += "&username=" + localStorage.getItem("username");

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	          
	          //update sub pub for home.html
	   		  var pub = JSON.parse(localStorage.getItem("pub"));
	   		  pub.push(JSON.parse("{\"name\":\""+topicname+"\"}"));
	  		  localStorage.setItem("pub", JSON.stringify(pub));

	   		  var sub = JSON.parse(localStorage.getItem("sub"));
	  		  for (i=0;i<sub.length;i++){
	  			if (sub[i]["name"] == topicname){
	  				sub.splice(i, 1);
	  				break;
	  			}
	  		  }
	  		  localStorage.setItem("sub", JSON.stringify(sub));
	  		  
	  		  alert("You have unsubscribed topic:" + topicname);
	          //redirect to subtopic
	          //replace will not leaving this page in history stack, so when call backpage()
	          //in subtopic.html, it will directly return to home.html rather than pubtopic.html
	    	  window.location.replace(BaseURL + "pubtopic.html");
	        }
        }
        xhr.open("PUT", URL, true);
        xhr.send();
		
	}
	
	function pub(){
		var topicname = sessionStorage.getItem("click_topic");
		var textarea = document.getElementById("pubtext");
		var text = textarea.value;
		if (text){
			//pub new message to TopicServlet by POST
			var topicname = sessionStorage.getItem("click_topic");

	        var URL = BaseURL + "/TopicServlet";
	        URL += "?topic=" + sessionStorage.getItem("click_topic");

	        var xhr = new XMLHttpRequest();
	        xhr.onreadystatechange = function() {
		        if (xhr.readyState == 4 && xhr.status == 200) {
		          
		          //update sub for home.html
		   		  var sub = JSON.parse(localStorage.getItem("sub"));
		  		  for (i=0;i<sub.length;i++){
		  			if (sub[i]["name"] == topicname){
		  				sub[i]["new"] = "1";
		  				break;
		  			}
		  		  }
		  		  localStorage.setItem("sub", JSON.stringify(sub));
		  		  
		  		  alert("Successfuly publishing message!");
		    	  window.location.assign(BaseURL + "home.html");
		        }
	        }
	        xhr.open("POST", URL, true);
	        //xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	        xhr.send(text);
			
		}
		else{
			alert("The content of message cannot be empty!");
		}
		
	}
	
	
	
	