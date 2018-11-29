package kr.ac.kaist.testonpr.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	int lineNumber = 0;
	while ((inLine = inReader.readLine()) != null) {
		
		
		
	}
	inReader.close();
	
  }
  
}
