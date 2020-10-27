package Objects;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	Socket connectedSocket;
	char connectedType;
	ObjectOutputStream connectedOutput;
	
	public Connection(Socket connectedSocket, char connectedType, ObjectOutputStream connectedOutput) {
		this.connectedSocket = connectedSocket;
		this.connectedType = connectedType;
		this.connectedOutput = connectedOutput;
	}
	public ObjectOutputStream getConnectedOutput() {
		return connectedOutput;	
	}
	public char getType() {
		return connectedType;
	}
	public Socket getConnectedSocket() {
		return connectedSocket;
	}
}
