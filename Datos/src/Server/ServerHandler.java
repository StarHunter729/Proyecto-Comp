package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Objects.Message;

public class ServerHandler implements Runnable{
	public Socket connectedSocket;
	public Server fatherServer;
	public ObjectOutputStream oos; 
    public ObjectInputStream ois;	
    boolean isAlive;
	private ObjectOutputStream oosCell;
	@SuppressWarnings("unused")
	private Socket handlerCell;
    
	public ServerHandler(Socket socket, Server server) throws IOException {
		this.connectedSocket = socket;
		this.fatherServer = server;
		oos = new ObjectOutputStream(this.connectedSocket.getOutputStream());
		reportAliveStatus();
	}
	@Override
	public void run() {
		try {
			getMessage();
		} catch (ClassNotFoundException e) {
			System.out.println("Coudln't listen for another message");
		}
	}
	public void reportAliveStatus() {
		Message msg = new Message(1);
		fatherServer.ForwardMessage(connectedSocket, oos, msg);
		this.isAlive = true;
	}
	
	public void getMessage() throws ClassNotFoundException {
		System.out.println("Listening for messages");
		try {
			this.ois = new ObjectInputStream(this.connectedSocket.getInputStream());
		} catch (IOException e1) {
			this.isAlive = false;
			System.out.println("Lost connection");
		}
		while(isAlive) {
			Message receivedMessage; 
			try {
				receivedMessage = (Message) ois.readObject();
				System.out.println("Received message " + receivedMessage.getMsg() + " CC: " + receivedMessage.getContentCode());
				processObject(receivedMessage);
				
			} catch (IOException e) {
				System.out.println("Error reading message");
				break;
			}
		}
	}

	public void processObject(Message msg) {
		int objCase = msg.getContentCode();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		switch(objCase) {
			case 1: //first Message from a Node
			{
				fatherServer.addToTable(connectedSocket, 'n' , oos);
				//System.out.println("Received First Message from a Node" );
				break;
			}
			case 2://first Message from an Interface
			{
				fatherServer.addToTable(connectedSocket, 'i' , oos);
				//System.out.println("Received First Message from an Interface" );
				break;
			}
			case 3://first Message from a Cell
			{
				fatherServer.addToTable(connectedSocket, 'c' , oos);
				if(this.oosCell != null) {
					try {
						this.oosCell.writeObject(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//System.out.println("Received First Message from a Cell" );
				break;
			}

			case 4://Message contains an acknowledgement 
			{
			if(!msg.getOriginal())	
				{
					fatherServer.sendInterface(msg);
				}
			else
				{
					fatherServer.Broadcast(msg);
					fatherServer.sendInterface(msg);
				}
				System.out.println("Received an acknowledgement");
				break;
			}
			case 5://Message contains a sum 
			{
				if(!msg.getOriginal())	
					{
						fatherServer.sendCell(msg);
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received the sum operation " + msg.getMsg());				
				break;
			}
			case 6://Message contains a sub 
			{
				if(!msg.getOriginal())
					{
						fatherServer.sendCell(msg);
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received the sub operation " + msg.getMsg());
				break;
			}
			case 7://Message contains a mul 
			{
				if(!msg.getOriginal())
					{
						fatherServer.sendCell(msg);
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received the mul operation " + msg.getMsg());
				break;		
			}
			case 8://Message contains a div 
			{
				if(!msg.getOriginal())
					{
						fatherServer.sendCell(msg);
						
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received the div operation " + msg.getMsg());
				break;
			}
			case 9://Message contains the result of an operation 
			{
				System.out.println("Received the result for a previously sent operation " + msg.getMsg() + " with the CC: " + msg.getContentCode());
				fatherServer.sendInterface(msg);
				break;
			}
			case 10://Message contains a div 
			{
				if(!msg.getOriginal())
					{
						fatherServer.sendCell(msg);
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received a request for acknoledgments for " + msg.getMsg());
				break;
			}
			case 14://Message contains a div 
			{
				if(!msg.getOriginal())
					{
						fatherServer.sendCell(msg);
						
					}
				else
					{
						fatherServer.Broadcast(msg);
						fatherServer.sendCell(msg);
					}
				System.out.println("Received a cloning request for a cell ");
				break;
			}
			
			case 15:
			{
				if(!msg.getOriginal())
				{
					fatherServer.sendCell(msg);
					
				}
			else
				{
					fatherServer.Broadcast(msg);
					fatherServer.sendCell(msg);
				}
			System.out.println("Received a request to update the status of a mother cell");
			break;
			}
			
			case 16:{
				System.out.println("First contact from a handler of cells");
				this.handlerCell = connectedSocket;
				this.oosCell = oos;
			}
		}
	}
}
