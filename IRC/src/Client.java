
public class Client {
	private Integer ID = null;
	private String name = null;
	private String ActualChannel = "";
	
	public Client(Integer ID, String Name) {
		this.ID = ID;
		this.name = Integer.toString(ID);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public Integer getId(){
		return ID;
	}
	
	public String toString() {
		return ID+": "+name;
	}
	
	public void setChannel(String Name) {
		ActualChannel = Name;
	}
	
	public String getChannel() {
		return ActualChannel;
	}
}
