import java.net.*;
import java.util.ArrayList;
import java.awt.List;
import java.io.*;

public class ChatServer implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private static ArrayList<Channel> Channels =new ArrayList<Channel>();   
   
   /*Abre um Socket na porta desejada e tenta startar o servidor*/
   public ChatServer(int port){  
	   try{  
		   System.out.println("Binding to port " + port + ", please wait  ...");
		   server = new ServerSocket(port);  
		   System.out.println("Server started: " + server);
		   start(); 
	   }catch(IOException ioe){  
		   System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
   
   /*Quando a thread é iniciada fica esperando por novos clientes*/
   public void run()
   {  
	  while (thread != null)
      {  try{
    	  	System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); 
         }catch(IOException ioe){  
        	System.out.println("Server accept error: " + ioe); 
        	stop(); 
         }
      }
   }
      
   public void start(){  
	   if (thread == null){  
		   thread = new Thread(this); 
	       thread.start();
	   }
   }
   
   public void stop(){ 
	   if (thread != null){
		   thread.stop(); 
	       thread = null;
	   }
   }
   
   private int findClient(int ID){  
	   for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   private int findClientByName(String Name){  
	   for (int i = 0; i < clientCount; i++) {		 		
		   if (clients[i].getCli().getName().equals(Name))
            return i;
	   }
      return -1;
   }
   
   /* Clase que gerencia as mensagens e comandos do usuário */
   public synchronized void handle(Client cli, String input){  
	   if (input.equals(".bye")){  
		   sendToAll(cli,cli.getName()+" get out from server");
		   clients[findClient(cli.getId())].send(".bye");
		   remove(cli.getId()); 
       }else{    	    	   
    	 String[] split = input.split(" ");
    	 String action = input;
    	 String message = "";
    	 if(split.length >=  1){
    		 action = split[0];
    	 }
    	 switch(action){
    	 	case "/nick":    	 	
    	 		message = "Type a nickname";
    	 		if(split.length > 1) {  
	    	 		String old = cli.getName();
	    	 		cli.setName(split[1]);
	    	 		message = old + " change his name to " + cli.getName();
    	 		}
    	 		sendToAll(cli,message);    	 		
    	 		break;
    	 	case "/list":
    	 		message = "Server list:\n";
    	 		for (Channel item : Channels) {    	            
    	        	message += item.getName()+"\n";
    	        }    	 		    	 		 
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	case "/join":
    	 		message = "Type a channel name";
    	 		if(split.length > 1) {    	 			
    	 			Channel ch = findChannel(split[1]);    	 			
    	 			if(ch != null) {    	 				
    	 				if(ch.addUser(cli)) {
    	 					cli.setChannel(ch.getName());
    	 					clients[findClient(cli.getId())].updateCli(cli);
    	 					message = "You are in "+ch.getName()+" channel";
    	 				}else {
    	 					message = "Something went wrong!";
    	 				}    	 				
    	 			}else{
    	 				message = "Channel not found";
    	 			};
    	 		}    	 		 
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	case "/names":
    	 		message = "You need to get in a channel to see the users";
    	 		if(cli.getChannel()!="") {
    	 			Channel ch = findChannel(cli.getChannel());
    	 			message = ch.getUsers(cli.getName()); 
    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	case "/msg":
    	 		message = "To sent a private message use: /msg username message";
	    	 		if(cli.getChannel() != "") {
	    	 			if(split.length >= 3) { 
		    	 			boolean find = false;
		    	 			Integer cliDest = findClientByName(split[1]);		    
		    	 			if(cliDest != -1){
		    	 				message = "<"+split[1]+"> "+ input.replace("/msg","").replace(split[1],"");
		    	 				clients[cliDest].send(message);
		    	 				find = true;
		    	 			}
		    	 			if(!find)
		    	 				message = "Nickname not found!";
	    	 			}
	    	 		}else {
	    	 			message = "To sent private messages you need to be in a channel.";
	    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;  
    	 	case "/part":
    	 		message = "First you need to enter in a channel to take a part.";
    	 		if(cli.getChannel()!= "") {
    	 			String chName = cli.getChannel(); 
    	 			cli.setChannel("");
    	 			Channel ch = findChannel(chName);
    	 			ch.removeUser(cli); 
    	 			message = cli.getName()+" get out from "+chName;
    	 			sendToAll(cli,message);    	 		
    	 			break;
    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;  
    	 	case "/quit":
    	 		sendToAll(cli,cli.getName()+" get out from server");
    	 		clients[findClient(cli.getId())].send(".bye");
    			remove(cli.getId());    			    	 		
    			break;
    	 	case "/kick":
    	 		message = "You need to enter in a channel and be Admin to kick users.";
    	 		if(cli.getChannel()!= "") {
    	 			if(split.length > 1) {    
    	 				Channel ch = findChannel(cli.getChannel());
    	 				if(findClientByName(split[1])!= -1) {
	    	 				Client userToKick = clients[findClientByName(split[1])].getCli(); 
	    	 				if(ch.kickUser(cli, userToKick)) {
	    	 					clients[findClientByName(split[1])].getCli().setChannel("");
	    	 					sendToAll(cli,userToKick.getName()+" as kicked from "+ ch.getName());
	    	 				}else {
	    	 					message = "You need to be channel admin to kick a user";
	    	 				}
    	 				}else {
    	 					message = "Nickname to kick not found!";
    	 				}
    	 			}else {
    	 				message = "You need to inform a valid username";
    	 			}
    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	case "/create":
    	 		message = "You need to be out of a channel to create a new one.";
    	 		if(cli.getChannel()== "") {
    	 			if(split.length > 1) {  
    	 				Channels.add(new Channel(split[1],cli));
    	 				message = "Your Channel has been created, use /join to get in";
    	 			}else {
    	 				message = "You need to inform a Channel name";
    	 			}
    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	case "/remove":
    	 		message = "You need to be out of a channel and be admin to remove.";
    	 		if(cli.getChannel()== "") {
    	 			if(split.length > 1) {      	 				
    	 				Channel ch = findChannel(split[1]);
    	 				if(ch != null) {
	    	 				if(!ch.isFixed()) {
	    	 					if(ch.getAdmin().equals(cli)) {
	    	 						for (int i = 0; i < clientCount; i++) {
	    	 							Client userToKick = clients[i].getCli();
	    	 							if(userToKick.getChannel().equals(split[1])){
			    	    	 				if(ch.kickUser(cli, userToKick)) {
			    	    	 					clients[i].getCli().setChannel("");
			    	    	 					sendToAll(cli,userToKick.getName()+" as kicked from "+ ch.getName());
			    	    	 				}
	    	 							}
	    	 						}
	    	 						Channels.remove(ch);
	    	 						message = "Your Channel has been removed.";
	    	 					}else {
	    	 						message = "You are not the admin of this Channel! You can't remove!";
	    	 					}
	    	 				}else {
	    	 					message = "This Channel is Fixed you can't remove!";
	    	 				}
    	 				}else {
    	 					message = "Channel not found!";
    	 				}
    	 			}else {
    	 				message = "You need to inform a Channel name";
    	 			}
    	 		}
    	 		clients[findClient(cli.getId())].send(message);
    	 		break;
    	 	default:	         
	        	 sendToAll(cli,cli.getName() + ": " + input);
    	 }
       }
   }
   
   public Channel findChannel(String name) {
	   Channel ch = null;
	   for (Channel item : Channels) {		   		  
       	if(item.getName().equals(name)) {       		
       		ch = item;
       	}	
       }  
	   return ch;
   }
   
   /*Envia para todos os usuários*/
   public void sendToAll(Client cli,String input) {
	   for (int i = 0; i < clientCount; i++) {
		   if(cli.getChannel().equals(clients[i].getCli().getChannel())) 
			clients[i].send(input);
	   }    
   }   
   
   public synchronized void remove(int ID){  
	   int pos = findClient(ID);
	   if (pos >= 0){  
		   ChatServerThread toTerminate = clients[pos];
		   System.out.println("Removing client thread " + ID + " at " + pos);
		   if (pos < clientCount-1)
			   for (int i = pos+1; i < clientCount; i++)
				   clients[i-1] = clients[i];
		   clientCount--;
		   try{  
			   toTerminate.close(); 
		   }catch(IOException ioe){  
			   System.out.println("Error closing thread: " + ioe); 
		   }
		   toTerminate.stop(); }
   }
   
   /*Quando um novo cliente se conecta verifica se há espaço no servidor e 
    * inicia uma Thread para se comunicar exclusivamente com aquele cliente*/
   private void addThread(Socket socket){  
	   if (clientCount < clients.length){  
		   System.out.println("Client accepted: " + socket);
		   clients[clientCount] = new ChatServerThread(this, socket);
         try{
        	/*Comandos utilizados com o objeto Clients são gerenciados 
        	 * pela Classe ChatServerThread*/
        	 clients[clientCount].open(); 
        	 clients[clientCount].start();  
        	 clientCount++; 
         }catch(IOException ioe){
        	 System.out.println("Error opening thread: " + ioe); 
         } 
	   }else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   
   /*Verifica se foi setado nos argumentos a porta e inicia o servidor*/
   public static void main(String args[]) {
	  ChatServer server = null;	  	  	  
	  Channels.add(new Channel("Redes"));
	  Channels.add(new Channel("SisOp"));
	  Channels.add(new Channel("IRC"));
	  
      if (args.length != 1)
         System.out.println("Usage: java ChatServer port");
      else
         server = new ChatServer(Integer.parseInt(args[0]));   
   }
}