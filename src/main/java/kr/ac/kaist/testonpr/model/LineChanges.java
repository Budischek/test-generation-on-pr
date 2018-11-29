package kr.ac.kaist.testonpr.model;
import java.util.ArrayList;

public class LineChanges {

	String name;
	public ArrayList<Integer> added, removed;
	
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
	
	public ArrayList<Integer> getRemovedList() {
		return removed;
	}
	
	public ArrayList<Integer> getAddedList() {
		
		int tempLine = added.get(0);
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int newLine;
		for(int i = 1; i < added.size(); i++) {
			if((newLine = added.get(i)) != tempLine) {
				temp.add(tempLine);
				tempLine = newLine;
			}
		}
		
		return temp;
	}
}
