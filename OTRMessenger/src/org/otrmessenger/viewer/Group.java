package org.otrmessenger.viewer;
import java.util.ArrayList;

public class Group {
	private ArrayList<User>members;
	private String name;

	public Group(){
	    this.name = "";
	    this.members = new ArrayList<User>();
	}
	
	public Group(String n){
	    this.members = new ArrayList<User>();
	    String list[] = n.split("\\n");
	    this.name = list[0];
	    for (String l: list) {
	        l = l.replaceAll("\\s+", "");
	        this.members.add(new User(l));
	    }
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean addUser(User usr){
	
		return members.add(usr);	
	}

	public boolean delUser(User usr){
				
		return members.remove(usr);
	}

	//not in class diagram
	public boolean isInGroup(User usr){
		return members.contains(usr);
	}
	
	public void printMembers(){		
		System.out.println(name); 
		for (int index = 0; index<members.size(); index++){
			System.out.println("\n" + members.get(index).username);
		}
	}

	//new function not in Class diagram
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(name + ":");
		//build a string with string builder name+"\n";
		for (User user : members)
			builder.append("\n\t" + user.username);	
		return builder.toString(); 
	}
}