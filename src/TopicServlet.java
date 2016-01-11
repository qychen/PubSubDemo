

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TopicServlet
 */
@WebServlet("/TopicServlet")
public class TopicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TopicServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected static ArrayList<String> GetMessage(String topic){
    	ArrayList<String> Message = new ArrayList<String>();

	    //get message in this topic from file
	    java.io.InputStream input = TopicServlet.class.getResourceAsStream("topic/"+topic+".txt");
    	BufferedReader br = new BufferedReader(new InputStreamReader(input));
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    	    	Message.add(line.trim());
    	        line = br.readLine();
    	    }
    	    br.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return Message;
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		if (request.getParameterMap().containsKey("username")){
			//return unread message for user
			String username = request.getParameter("username").trim();
			String topic = request.getParameter("topic").trim();
			String res = "";
			
			ArrayList<String> Message = GetMessage(topic);
	    	ArrayList<String> usertopic = new ArrayList<String>();
		    java.io.InputStream input = TopicServlet.class.getResourceAsStream("userdata/"+username+".txt");
	    	BufferedReader br = new BufferedReader(new InputStreamReader(input));
    		String newfile = "";
	    	try {
	    	    String line = br.readLine();
	    	    while (line != null) {
	    	    	String newline = line;
	    	    	if (line.trim().split(",")[0].equals(topic)){
	    	    		ArrayList<String> read = new ArrayList<String>(Arrays.asList(line.trim().split(",")));
	    	    		int i = 0;
	    	    		for(i=0;i<Message.size();i++){
	    	    			if(!read.contains(Message.get(i).trim().split(";;")[0])){
	    	    				res = res + Message.get(i).split(";;")[1] + "\n";
	    	    				newline += "," + Message.get(i).trim().split(";;")[0];
	    	    			}
	    	    		}
 	    	    	}
	    	    	newfile = newfile + newline + "\n";
	    	        line = br.readLine();
	    	    }
	    	    br.close();
	    	    
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    //update userdata: unread => read
	    	//ServletContext servletContext = request.getSession().getServletContext();
	    	String relativePath = "WEB-INF/classes/userdata/"+username+".txt";
	    	String absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
	    	File userfile = new File(absoluteDiskPath);
	    	FileWriter fWriter = new FileWriter(userfile, false); 
	    	fWriter.write(newfile);
	    	fWriter.close();
			
	    	System.out.println(res);
			out.println(res); 
		}
		else{
			//get all message for this topic
			String topic = request.getParameter("topic").trim();
			ArrayList<String> Message = GetMessage(topic);
			int i = 0;
    		for(i=0;i<Message.size();i++){
    			out.println(Message.get(i).trim().split(";;")[1]);
    		}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String message = request.getReader().readLine();
		String topic = request.getParameter("topic").trim();
		ArrayList<String> Message = GetMessage(topic);
		String newline = Integer.toString(Message.size()+1) + ";;" + request.getReader().readLine().trim();
		
		//append new message to topic.txt
    	String relativePath = "WEB-INF/classes/topic/"+topic+".txt";
    	String absoluteDiskPath = getServletContext().getRealPath("/") + relativePath;
    	File userfile = new File(absoluteDiskPath);
    	FileWriter fWriter = new FileWriter(userfile, true); 
    	fWriter.write(newline+"\n");
    	fWriter.close();
		
		
	}

}










