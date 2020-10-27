package sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import Objects.Message;
import Objects.acknowlegmentTable;
import Objects.readConfig;

public class interfaceHandler implements Runnable{
	public Socket connectedSocket;
	public Controller fatherInterface;
	public String currentOperation;
	public ObjectOutputStream oos; 
    public ObjectInputStream ois;	
    boolean isAlive = true;
	int Min = 5000;
	int Max = 5100;
	acknowlegmentTable ack = new acknowlegmentTable(); 
	
	int requiredSum, requiredSub, requiredMul, requiredDiv;
	
	public interfaceHandler(Controller c) {
		this.fatherInterface = c;
		readConfig newConfiguration = new readConfig();
        
		try {
			
        	int [] initialConfig = newConfiguration.loadConfig();
			
			requiredSum = initialConfig[0];
			requiredSub = initialConfig[1];
			requiredMul = initialConfig[2];
			requiredDiv = initialConfig[3];
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
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
	
	public void reportAliveStatus() {
		Message msg = new Message(2);
		System.out.println("Sent initial Message with the content " + msg.getMsg());
		forwardMessage(msg);
	}
	
	public void setOperation(String s) {
		this.currentOperation = s;
		this.fatherInterface.setDesc("Id: " + this.currentOperation + "\n was assigned to the current operation. ");
	}
	
	public void forwardMessage(Message msg) {
		try {
			
			switch(msg.getContentCode()) {
				
			case 5:
			{
					if(ack.getSumAcknowledgements() < requiredSum) {
						System.out.println("The number of acknowledgements is inferior to the required one. ");
						Message cloneReq = new Message();
						cloneReq.setContentCode(14);
						cloneReq.setMessage("s");
						cloneReq.setFirst(requiredSum);
						oos.writeObject(cloneReq);
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				
				case 6:{
					if(ack.getSumAcknowledgements() < requiredSub) {
						System.out.println("The number of acknowledgements is inferior to the required one. ");
						Message cloneReq = new Message();
						cloneReq.setContentCode(14);
						cloneReq.setMessage("r");
						cloneReq.setFirst(requiredSub);
						oos.writeObject(cloneReq);
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				
				case 7:{
					if(ack.getSumAcknowledgements() < requiredMul) {
						System.out.println("The number of acknowledgements is inferior to the required one. ");
						Message cloneReq = new Message();
						cloneReq.setContentCode(14);
						cloneReq.setMessage("d");
						cloneReq.setFirst(requiredMul);
						oos.writeObject(cloneReq);
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				
				case 8:{
					if(ack.getSumAcknowledgements() < requiredDiv) {
						System.out.println("The number of acknowledgements is inferior to the required one. ");
						Message cloneReq = new Message();
						cloneReq.setContentCode(14);
						cloneReq.setMessage("d");
						cloneReq.setFirst(requiredDiv);
						oos.writeObject(cloneReq);
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					break;
				}
			}
			
			oos.writeObject(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
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
			case 3://first Message from a Cell
			case 5://Message contains a sum 
			case 6://Message contains a sub 
			case 7://Message contains a mul 
			case 8://Message contains a div 
			{
				System.out.println("Message arrived with the content +" + msg.getMsg());
				System.out.println("Message was dropped");
				break;
			}
			case 9://Message contains the result of an operation 
			{
				if(msg.getEventId().equals(this.currentOperation)) {
					fatherInterface.setAnswer(msg.getMsg());	
				}				
				else {
					System.out.println(" The id " + msg.getEventId() + " =! " + this.currentOperation);
				}
				System.out.println("Message arrived with the response of a previous operation: " + msg.getMsg());
				break;
			}
			case 4://Message contains an acknowledgement 
			{
				System.out.println("Message received acknowledment from: \n" + msg.getEventId());
				switch( msg.getMsg() ) {
					case "s":
					{
						ack.sumList(msg.getEventId(), (int)msg.getFirst());
						break;
					}
					
					case "r":
					{
						ack.subList(msg.getEventId() , (int)msg.getFirst());
						break;
					}
					
					case "m":
					{
						ack.mulList(msg.getEventId(), (int)msg.getFirst());
						break;
					}
					
					case "d":
					{
						ack.divList(msg.getEventId(), (int)msg.getFirst());
						break;
					}
				}
				break;
				
			}
			case -1:
			{
				System.out.println("Message received " + msg.getMsg());
				break;
			}
		}
	}
}
