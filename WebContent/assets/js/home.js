var BaseURL = "http://localhost:8080/PubSubDemo/";	

	function Logout(){
		localStorage.clear();
		sessionStorage.clear();
		window.location.replace(BaseURL + "index.html");
	}

	//get user info
	var sub = JSON.parse(localStorage.getItem("sub"));
	var pub = JSON.parse(localStorage.getItem("pub"));
	var list = document.getElementById("sublist");
	var i = 0;
	for (i=0;i<sub.length;i++){
		var entry = document.createElement('li');
		entry.onclick = click_sub;
		entry.style.cursor = "pointer";
		if (sub[i]["new"] == "1"){
			entry.className = "unread";
			entry.appendChild(document.createTextNode(sub[i]["name"]+" (New Message)"));
		}
		else{
			entry.className = "read";
			entry.appendChild(document.createTextNode(sub[i]["name"]));
		}
		list.appendChild(entry);
	}
	list = document.getElementById("publist");
	for (i=0;i<pub.length;i++){
		var entry = document.createElement('li');
		entry.onclick = click_pub;
		entry.style.cursor = "pointer";
		entry.appendChild(document.createTextNode(pub[i]["name"]));
		list.appendChild(entry);
	}
	
	function click_sub(){
		//console.log(this.innerHTML);
		if (this.className == "unread"){
			topicname = this.innerHTML;
			topicname = topicname.substring(0, topicname.length-14).trim();
			sessionStorage.setItem("click_topic", topicname);
			//get unread message from TopicServlet
            var URL = BaseURL + "/TopicServlet";
            URL += "?username=" + localStorage.getItem("username");
            URL += "&topic=" + topicname;

            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
		        if (xhr.readyState == 4 && xhr.status == 200) {
		          //response
		          response = this.responseText.trim();
		          console.log(response);
		          sessionStorage.setItem("unreadMessage", response);
		   		  //change this topic to read
		   		  var sub = JSON.parse(localStorage.getItem("sub"));
		  		  for (i=0;i<sub.length;i++){
		  			if (sub[i]["name"] == topicname){
		  				sub[i]["new"] = "0";
		  				break;
		  			}
		  		  }
		  		  localStorage.setItem("sub", JSON.stringify(sub));
		    	  window.location.assign(BaseURL + "subtopic.html");
		        }
            }
            xhr.open("GET", URL, true);
            xhr.send();
		}
		else{
			topicname = this.innerHTML;
			sessionStorage.setItem("click_topic", topicname);
	    	window.location.assign(BaseURL + "subtopic.html");
			
		}
	}
	
	function click_pub(){
		topicname = this.innerHTML;
		sessionStorage.setItem("click_topic", topicname);
    	window.location.assign(BaseURL + "pubtopic.html");
	}
	
	
	