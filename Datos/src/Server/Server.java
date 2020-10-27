package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Objects.Connection;
import Objects.Message;

public class Server{
	int Min = 5000, Max = 5100;
	boolean isAlive;
	ServerSocket serverSocket; 
	//ArrayList<Connection> connectedSockets = new ArrayList<Connection>();
	ArrayList<Connection> connectedNodes = new ArrayList<Connection>();
	ArrayList<Connection> connectedCells = new ArrayList<Connection>();
	ArrayList<Connection> connectedInterfaces = new ArrayList<Connection>();
	
	int localSocketPort;
	int totalConnections;
	Socket localSocket;
	Socket dummySocket;
	
	public void getServerSocket() {
		for(int port = this.Min; port <= this.Max; port++) {
			try {
				this.serverSocket = new ServerSocket(port);
				System.out.println("Connection at port: " + port);
				this.localSocketPort = port;
				this.isAlive = true;
				break;
			} catch (IOException e) {
				System.out.println("Not possible to connect at port: " + port);
			}
		}
	}
	public void connectToExisting() {
		for(int port = this.Min; port <= this.Max; port++) {
			if(port != this.localSocketPort) {
				try {
					Socket tempSocket = new Socket("127.0.0.1", port);
						System.out.println("Connected to previously existing node at port " + port);
					new Thread(new ServerHandler(tempSocket, this)).start();;
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.out.println("Unknown Host Exception");
				} catch (IOException e) {
					//System.out.println("");
					//e.printStackTrace();
				}
			}
		}
	}
	public void keepListening() {
		while(this.isAlive) {
			try {
				this.dummySocket = this.serverSocket.accept();
				System.out.println("New connection found at port: " + this.dummySocket.getPort());
				new Thread(new ServerHandler(this.dummySocket, this)).start();;
				
			} catch (IOException e) {
				System.out.println("Coudln't accept new connection");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void addToTable(Socket connectedSocket, char connectedType, ObjectOutputStream connectedOutput) {
		Connection newConnection = new Connection(connectedSocket, connectedType, connectedOutput);
		System.out.println("Received a petition to be added to the table from a " + connectedType);
		
		//connectedSockets.add(newConnection);
		switch(connectedType) {
			case 'n':{
				connectedNodes.add(newConnection);
				System.out.println("Node was succesfully added to the NodeList");
				break;
			}
			case 'i':{
				connectedInterfaces.add(newConnection);
				System.out.println("Node was succesfully added to the InterfaceList");
				break;
			}
			case 'c':{
				connectedCells.add(newConnection);
				System.out.println("Node was succesfully added to the CellList");
				break;
			}
		}
		
		//System.out.println("The connection at " + connectedSocket.getLocalPort() + " was successfully added!");
		Message msg = new Message(-1);
		try {
			connectedOutput.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void Broadcast( Message msg ) {
		//System.out.println("Got here ");		
		for(int i = 0; i < connectedNodes.size(); i++) {
			if(msg.getOriginal()) {
				ForwardMessage(connectedNodes.get(i).getConnectedSocket(), connectedNodes.get(i).getConnectedOutput(), msg);
			}
		}
	}
	public synchronized void sendCell( Message msg ) {
		for(int i = 0; i < connectedCells.size(); i++) {
			ForwardMessage(connectedCells.get(i).getConnectedSocket(), connectedCells.get(i).getConnectedOutput(), msg);
		}
	}
	public synchronized void sendInterface( Message msg ) {
		
		for(int i = 0; i < connectedInterfaces.size(); i++) {
			if(connectedInterfaces.get(i).getType() == 'i') {
				System.out.println("Sent message to interface");
				ForwardMessage(connectedInterfaces.get(i).getConnectedSocket(), connectedInterfaces.get(i).getConnectedOutput(), msg);
			}
		}
	}
	public synchronized void ForwardMessage(Socket tempSocket, ObjectOutputStream oos, Message msg ) {
		msg.updateOriginal();
		try {
			oos.writeObject(msg);
			System.out.println("Message sent with the following data: " + msg.getMsg());
		} catch (IOException e) {
			System.out.println("Error sending the message with the content: " + msg.getMsg());
		}
	}
}
