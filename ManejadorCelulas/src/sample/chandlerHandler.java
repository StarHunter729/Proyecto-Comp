package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Objects.Message;

public class chandlerHandler implements Runnable {

	public Socket connectedSocket;
	public cController fatherInterface;
	public ObjectOutputStream oos; 
    public ObjectInputStream ois;	
    boolean isAlive = true;
    int Min = 5000;
	int Max = 5100;	
    
	public chandlerHandler(cController cController) {
		this.fatherInterface = cController;
		System.out.println("Handler execution thread instanced");
	}

	public void connectToDataField() {
		for(int port = Min; port <= Max; port++) {
			try {
				connectedSocket =  new Socket("127.0.0.1", port);
				System.out.println("Found connection at port " + port);
				//fatherInterface.setDescription("Found connection at port " + port);
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	public void reportAliveStatus() {
		Message msg = new Message(19);
		System.out.println("Sent initial Message with the content " + msg.getMsg());
		forwardMessage(msg);
	}
	
	public void forwardMessage(Message msg) {
		if(msg != null)
		{
			try {
				oos.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Message is empty"); 
		}
	}
	
	public void getMessage() throws ClassNotFoundException {
		System.out.println("Listening for messages");

		while(isAlive) {
			Message receivedMessage; 
			try {
				receivedMessage = (Message) ois.readObject();
				System.out.println("Received message " + receivedMessage.getMsg());
				processObject(receivedMessage);
				
			} catch (IOException e) {
				System.out.println("Error reading message");
				break;
			}
		}
	}

	public void processObject(Message msg) {
		int objCase = msg.getContentCode();
		switch(objCase) {
			case 1: //first Message from a Node
			case 2://first Message from an Interface
			case 5://Message contains a sum 
			case 6://Message contains a sub 
			case 7://Message contains a mul 
			case 8://Message contains a div 
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
				{
					break;
				}
				
		case 3://first Message from a Cell
			{
				System.out.println("Found the cell " + msg.getMsg());
				break;
			}
		}
	}
	
	
	@Override
	public void run() {
		
		connectToDataField();
		try {
			ois = new ObjectInputStream(this.connectedSocket.getInputStream());
			oos = new ObjectOutputStream(this.connectedSocket.getOutputStream());
			reportAliveStatus();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			getMessage();
		} catch (ClassNotFoundException e) {
			System.out.println("Coudln't listen for another message");
		}
	}

}
