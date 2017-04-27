package org.otrmessenger.viewer;
import java.util.ArrayList;
import org.otrmessenger.viewer.Group;
import org.otrmessenger.viewer.User;

public class FriendsList {
	private ArrayList<Group> friends;
	private ArrayList<User> banned;
	
	//changed parameter to User
	public boolean banUser(User usr){
		banned.add(usr);
		for (Group grp : friends){
			if (grp.isInGroup(usr)){
				return grp.delUser(usr);
			}
		}
		return false;//it should never hit this?
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
	
}
