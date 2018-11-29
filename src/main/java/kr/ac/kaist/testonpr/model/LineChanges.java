package kr.ac.kaist.testonpr.model;
import java.util.ArrayList;

public class LineChanges {

	String name;
	public ArrayList added, removed;
	
	public LineChanges(String name) {
		this.name = name;
		added = new ArrayList<Integer>();
		removed = new ArrayList<Integer>();
	}
	
	public void newLine(int lineNumber) {
		added.add(lineNumber);
	}
	
	public void removedLine(int lineNumber) {
		removed.add(lineNumber);
	}
	
	
}
