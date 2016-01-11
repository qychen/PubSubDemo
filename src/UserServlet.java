

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.portable.InputStream;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet(description = "Deal with user requests like login, pub/sub and saving user data", urlPatterns = { "/UserServlet" })
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    private boolean ValidUser(String UserName) throws IOException{
	    ArrayList<String> UserList = new ArrayList<String>();
	    
	    java.io.InputStream input = UserServlet.class.getResourceAsStream("userlist.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(input));
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    	    	UserList.add(line.trim());
    	        line = br.readLine();
    	    }
    	    br.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	if (UserList.contains(UserName))
    		return true;
    	else
    		return false;
    }
    
    private String GetUserInfo(String username){
    	//generate response json as string
	    ArrayList<String> TopicList = new ArrayList<String>();
	    ArrayList<String> SubList = new ArrayList<String>();
	    String res = "{";
	    
	    //get topic list from file
	    java.io.InputStream input = UserServlet.class.getResourceAsStream("topiclist.txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(input));
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    	    	TopicList.add(line.trim());
    	        line = br.readLine();
    	    }
    	    br.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//get user subscribed topics from file
    	res += "\"sub\": [";
    	String FileName = "userdata/" + username + ".txt";
	    input = UserServlet.class.getResourceAsStream(FileName);
    	br = new BufferedReader(new InputStreamReader(input));
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    		    String[] topic = new String[20];
    	    	//check if there are new data for this topic
    	    	topic = line.trim().split(",");
    	    	SubList.add(topic[0]);
    	    	//read topic file
    	    	String TopicFileName = "topic/" + topic[0] + ".txt";
    		    java.io.InputStream input1 = UserServlet.class.getResourceAsStream(TopicFileName);
    	    	BufferedReader br1 = new BufferedReader(new InputStreamReader(input1));
        	    String tline = br1.readLine();
        	    int sum = 0;
        	    while (tline != null) {
        	    	sum += 1;
        	        tline = br1.readLine();
        	    }
        	    br1.close();
        	    if ((topic.length-1) == sum)
        	    	//no new data
        	    	res += "{\"name\": \"" + topic[0] + "\", \"new\": \"0\"}, ";
        	    else
        	    	//new data
        	    	res += "{\"name\": \"" + topic[0] + "\", \"new\": \"1\"}, ";
    	    	
    	        line = br.readLine();
    	    }
    	    br.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//get pub list
    	if (res.charAt(res.length()-1) != '[')
    		res = res.substring(0, res.length()-2);
    	res += "], \"pub\": [";
    	int i=0;
    	for (i=0;i<TopicList.size();i++){
    		if (!SubList.contains(TopicList.get(i)))
    			res += "{\"name\": \"" + TopicList.get(i) + "\"}, ";
    	}
    	if (res.charAt(res.length()-1) != '[')
    		res = res.substring(0, res.length()-2) + "]}";
    	else 
    		res = res + "]}";
    	return res;
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username").trim();
		PrintWriter out = response.getWriter();
		if (request.getParameterMap().containsKey("flag")){
			//new user
			if (ValidUser(username))
				out.println("no");
			else{
				out.println("yes");
				//append new user to userlist.txt
		    	String relativePath = "WEB-INF/classes/userlist.txt";
		    	String absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
		    	File userfile = new File(absoluteDiskPath);
		    	FileWriter fWriter = new FileWriter(userfile, true); 
		    	fWriter.write(username+"\n");
		    	fWriter.close();
		    	
		    	//create user.txt in userdata file
		    	relativePath = "WEB-INF/classes/userdata/"+username+".txt";
		    	absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
		    	userfile = new File(absoluteDiskPath);
		    	userfile.createNewFile();
			}
		}
		else{
			//login
			//System.out.println(username);
			if (ValidUser(username)){
				out.println("yes");
				String res = GetUserInfo(username);
				out.println(res);
			}
			else
				out.println("no");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//sub new topic
		String username = request.getParameter("username").trim();
		String topic = request.getParameter("topic").trim();
		PrintWriter out = response.getWriter();
		String newline = topic;
		
    	//return all message in this topic
    	ArrayList<String> Message = TopicServlet.GetMessage(topic);
		int i = 0;
		for(i=0;i<Message.size();i++){
			out.println(Message.get(i).trim().split(";;")[1]);
			newline += "," + Message.get(i).trim().split(";;")[0];
		}
		
		//update user.txt
    	String relativePath = "WEB-INF/classes/userdata/"+username+".txt";
    	String absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
    	File userfile = new File(absoluteDiskPath);
    	FileWriter fWriter = new FileWriter(userfile, true); 
    	fWriter.write(newline+"\n");
    	fWriter.close();
		
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//unsub topic
		String username = request.getParameter("username").trim();
		String topic = request.getParameter("topic").trim();

	    java.io.InputStream input = TopicServlet.class.getResourceAsStream("userdata/"+username+".txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String newfile = "";
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    	    	if (!line.trim().split(",")[0].equals(topic))
        	    	newfile = newfile + line + "\n";
    	        line = br.readLine();
    	    }
    	    br.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//update user.txt
    	String relativePath = "WEB-INF/classes/userdata/"+username+".txt";
    	String absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
    	File userfile = new File(absoluteDiskPath);
    	FileWriter fWriter = new FileWriter(userfile, false); 
    	fWriter.write(newfile);
    	fWriter.close();
		
	}
}





