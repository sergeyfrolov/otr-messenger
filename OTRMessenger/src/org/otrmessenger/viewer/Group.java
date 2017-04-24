package org.otrmessenger.viewer;
import java.util.ArrayList;

public class Group {
	private ArrayList<User>members;
	private String name;
	public Group(){
		
	}
	public boolean addUser(User usr){
		boolean confirm = false;
		int membersSize = members.size();
		members.add(usr);
		
		//if members.add(usr) successful change flag 
		int newSize = members.size();
		if (newSize == membersSize+1){
			confirm = true;}
		return confirm;
		
	}
	public boolean delUser(User usr){
		boolean confirm = false;
		int membersSize = members.size();
		members.remove(members.indexOf(usr));
		//if members.remove(index) successful change flag 
		int newSize = members.size();
		if (newSize == membersSize-1){
			confirm = true;}
		return confirm;
	}
	public void printMembers(){		
		System.out.println(members); 

	}
	//new function not in Class diagram
	public String toString(){
		return name;
	}
	
}
