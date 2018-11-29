package kr.ac.kaist.testonpr.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class PullRequest {
  public int number;
  
  public PullRequest(int number) {
	  this.number = number;
	  
	  try {
		sendGet();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  private void sendGet() throws IOException {
	String url = "https://patch-diff.githubusercontent.com/raw/Budischek/CS454/pull/" + number + ".diff";
		
	URL obj = new URL(url);
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
	con.setRequestMethod("GET");

	int responseCode = con.getResponseCode();
	
	System.out.println("\nSending 'GET' request to URL : " + url);
	System.out.println("Response Code : " + responseCode);


	
	BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
	String inLine;
	HashMap<String, LineChanges> classes = new HashMap<String, LineChanges>();
	
	while ((inLine = inReader.readLine()) != null) {
		
		if(inLine.contains("public class")) {
			String className = inLine.split(" ")[3];
			//System.out.println(className);
			LineChanges changes = new LineChanges(className);
			int lineNumber = 1;
			while (!(inLine = inReader.readLine()).startsWith("diff")) {
				if(inLine.startsWith("+")) changes.newLine(lineNumber);
				else if(inLine.startsWith("-")) { 
					changes.removedLine(lineNumber);
					lineNumber++;
				} else{
					lineNumber++;
				}
			}
			classes.put(className, changes);
			System.out.println(changes.removed);
		}
		LineChanges lc = (LineChanges) classes.get("Class0");

	}
	inReader.close();
  }
  
}
