package org.otrmessenger.viewer;
import java.util.ArrayList;
import org.otrmessenger.viewer.Group;
import org.otrmessenger.viewer.User;

public class FriendsList {
	private ArrayList<Group> friends;
	private ArrayList<User> banned;
	
	public boolean banUser(String username){
		boolean confirm = false;
		//TODO
		/*
		 * find the user with name == username
		 * banned.add(user with name == username);
		 * delFriendToGroup (user, group);
		*/
		return confirm;
	}
	//getFriendsList() is just the getter of friends, change it to getter?or change the attribute's name?
	public ArrayList<Group> getFriendsList(){
		return friends;
	}
	//getBannedList() is just the getter of banned, change it to getter?or change the attribute's name?
	public ArrayList<User> getBannedList(){
		return banned;
	}
	public boolean addFriendToGroup(User usr, Group grp){
		boolean confirm = false;
		//TODO
		/*if (!friends.contains(grp)){
		 *	print "error. group does not exist" 
		 *}else{ 
		 *	int indexOfGroup = friends.indexOf(grp);
		 *	int sizeOfGroup=friends[indexOfGroup].members.size();
		 *	friends[indexOfGroup].members.add(usr);
		 *	int newSize = friends[friends.indexOf(grp)].members.size();
		 *	if (newSize == sizeOfGroup +1){
		 *		confirm = True;
		 *	}
		 * */
		return confirm;
	}
	public boolean delFriendToGroup(User usr, Group grp){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	//delGroup takes as parameter a Group, shouldn't this be String for the group's attribute "name"?
	public boolean delGroup(Group groupName){
		boolean confirm = false;
		//TODO
		return confirm;
	}
}
