import java.awt.List;
import java.util.ArrayList;

public class Channel {
	private String name = null;
	private Client Admin = null;
	private ArrayList<Client> clientList = new ArrayList<Client>();
	private boolean fixed = false; 
	
	public Channel(String name){
		this.name = name;			
		this.fixed = true;
	}	
	
	public Channel(String name, Client Admin){
		this.name = name;
		this.Admin = Admin;		
	}
	
	public boolean isFixed() {
		return fixed;
	}
	
	public Client getAdmin(){		
		return Admin;
	}
	
	public String getName() {
		return name;
	}	
	
	public boolean addUser(Client cli){
		if(clientList.add(cli)){
			return true;
		}
		return false;
	}
	
	public boolean removeUser(Client cli){
		if(clientList.remove(cli)){
			return true;
		}
		return false;
	}
	
	public boolean kickUser(Client admin, Client cli){
		if(admin.equals(this.Admin)) {
			if(clientList.remove(cli)){
				return true;
			}
		}
		return false;
	}
	
	public String getUsers(String cliName) {
		String listOfUser = "List of Users:\n";
		for (Client item : clientList) {
			if(item.getName().equals(cliName))
				listOfUser += "*";
			listOfUser += item.getName()+"\n";
        }    	 		 
		return listOfUser;
	}
}
