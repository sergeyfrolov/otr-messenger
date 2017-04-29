package org.otrmessenger.viewer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otrmessenger.viewer.Group;
import org.otrmessenger.viewer.User;

public class FriendsList {
	private ArrayList<Group> friends;
	private ArrayList<User> banned;
	private String uname;
	
	public FriendsList(String u) {
	    this.uname = u;
	    this.friends = new ArrayList<Group>();
	    this.banned = new ArrayList<User>();
	    
	    BufferedReader br = null;
	    try {
            br = new BufferedReader(new FileReader("." + u + ".friends"));
        } catch (FileNotFoundException e) {
            System.out.println("no friends yet!");
            return;
        }
	    
	    String line;
	    try {
            Group g = new Group();
            while((line = br.readLine()) != null){
                // check is line has whitespace (if so its a username, not a group name)
                Pattern pattern = Pattern.compile("^\\s");
                Matcher matcher = pattern.matcher(line);
                boolean found = matcher.find();
                if (!found){
                    if (!g.getName().equals(""))
                        this.friends.add(g);
                    

                    g = new Group();
                    g.setName(line.replaceAll(":", ""));
                }
                else{
                    if (line.length() > 0){
                        g.addUser(new User(line.replaceAll("\\s+", "")));
                    }
                }
            }
            this.friends.add(g);

        } catch (IOException e) {
            e.printStackTrace();
        }
	    
	    try {
            br = new BufferedReader(new FileReader(".enemies"));
        } catch (FileNotFoundException e) {
            System.out.println("no banned users");
            return;
        }

	    try {
            while((line = br.readLine()) != null){
                this.banned.add(new User(line.replaceAll("\\s+", "")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	    
	}

	public FriendsList(){
	    this("");
	}
	
	public void save(){
	    File f = new File("." + this.uname + ".friends");
	    File en = new File(".enemies");
	    try {
            PrintWriter out = new PrintWriter(f);
            for (Group g: this.friends){
                out.write(g.toString() + "\n");
            }
            out.close();
            
            out = new PrintWriter(en);
            for (User b: this.banned){
                out.write(b.toString() + "\n");
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}

	//changed parameter to User
	public boolean banUser(User usr){
		this.banned.add(usr);
		for (Group grp : friends){
			if (grp.isInGroup(usr)){
				return grp.delUser(usr);
			}
		}
		return false;//it should never hit this? unless person isn't in a group.
	}
	//getFriendsList() is just the getter of friends, change it to getter?or change the attribute's name?
	public ArrayList<Group> getFriendsList(){
		return friends;
	}
	//getBannedList() is just the getter of banned, change it to getter?or change the attribute's name?
	public ArrayList<User> getBannedList(){
		return banned;
	}
	//do we need this new function not in Class diagram?
	public boolean addGroup(Group grp){			
		return friends.add(grp);	
		}
	//add an user to a specified group 
	public boolean addFriendToGroup(User usr, Group grp){
		
		if (!friends.contains(grp)){
		 	addGroup(grp);
		 	return addFriendToGroup(usr, grp);
		 }else{ 
		 	int indexOfGroup = friends.indexOf(grp);
		 	return friends.get(indexOfGroup).addUser(usr);
		 }
	}
	//deletes an user from a specified group
	public boolean delFriendFromGroup(User usr, Group grp){
		int indexOfGroup = friends.indexOf(grp);
	 	return friends.get(indexOfGroup).delUser(usr);
	}
	//delGroup takes as parameter a Group, shouldn't this be String for the group's attribute "name"?
	public boolean delGroup(Group groupName){
		return friends.remove(groupName);	
	}
	
	public int numFriends(){
	    int sum = 0;
	    for (Group g: this.friends){
	        sum += g.numMembers();
	    }
	    
	    return sum;
	}
	
	public Object[][] toObjectArray(){
	    Object[][] ret =  new Object[this.numFriends()][4];
	    
	    int ind = 0;
	    for(Group g: this.friends){
	        ArrayList<User> m = g.getMembers();
	        for (User u: m){
                Object[] o = {g.getName(), u.getUsername(), "GET", "DELETE"};
                ret[ind] = o;
                ind++;
	        }
	    }
	    
	    return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Groups:\n");
		for (Group g: this.friends)
		    builder.append(g.toString() + "\n");
		builder.append("Banned:\n");
		for (User u: this.banned)
		    builder.append("\t" + u.toString() + "\n");
		
		return builder.toString();
	}
	
}
