import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread{  
	private ChatServer       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  streamIn  =  null;
	private DataOutputStream streamOut = null;
	private Client cli = null;

   public ChatServerThread(ChatServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      cli = new Client(socket.getPort(), Integer.toString(socket.getPort()));
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   public int getID()
   {  return ID;
   }
   
   public Client getCli() {
	   return cli;
   }
   
   public void updateCli(Client cli) {
	   this.cli = cli;
   }
   
   /*Enquanto a Thread estiver ativa fica esperando mensagens do cliente*/
   public void run(){  
	   System.out.println("Server Thread " + ID + " running.");	   
	   while (true){  
		   try{  
			   server.handle(cli, streamIn.readUTF());/*Recebe a mensagem e envia para o servidor gerenciar*/
		   }catch(IOException ioe){  
			   System.out.println(ID + cli.getName() + " ERROR reading: " + ioe.getMessage());		   
			   server.remove(ID);
			   stop();
		   }
      }
   }
   
   /*O comando open é chamado para definir a entrada e saida das 
    * mensagens enviadas por um cliente*/
   public void open() throws IOException{  
	   streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	   streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }
   
   public void close() throws IOException{  
	   if (socket != null)    socket.close();
	   if (streamIn != null)  streamIn.close();
	   if (streamOut != null) streamOut.close();
   }
}