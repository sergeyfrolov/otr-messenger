import java.util.ArrayList;

public class FriendsList {
	private ArrayList<Group> friends;
	private ArrayList<User> banned;
	
	public boolean banUser(String username){
		boolean confirm = false;
		//TODO
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
