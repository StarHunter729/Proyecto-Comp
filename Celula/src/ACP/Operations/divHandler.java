package ACP.Operations;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;

import Objects.Message;
import Services.Divide;
import fileHandler.Clonation;
import fileHandler.fileValidation;

public class divHandler {
	
	int Min = 5000;
	int Max = 5010;
	int localPort;
	Socket localSocket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String fingerPrint;
	String temporalOperaton = "";
	double numDiv = 0;
	String fileFldr = "C:/temp/Services/Divide";
	String file = "C:/temp/Services/Divide/Divide.jar";
	String rawFile = "C:/temp/Services/Divide/Divide-Clone";
	
	public divHandler() {
		this.fingerPrint = getProcessId();
		this.numDiv = fileValidation.getNumServices(this.fileFldr);
	}

	public void outputStream() {
		try {
			oos = new ObjectOutputStream(localSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void connectToDataField() {
		for(int port = Min; port <= Max; port++) {
			try {
				localSocket =  new Socket("127.0.0.1", port);
				localPort = port;
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void reportAliveStatus() {
		Message msg = new Message(3);
		forwardMessage(msg);
		System.out.println("Sent initial message");
	}
	public void forwardMessage( Message msg) {
		try {
			oos.writeObject(msg);
			System.out.println("A message was sent with the msg being " + msg.getMsg());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not send message");
			e.printStackTrace();
		}
	}
	public void getMessage() throws ClassNotFoundException {
		System.out.println("Listening for messages");
		try {
			ois = new ObjectInputStream(localSocket.getInputStream());
		} catch (IOException e1) {
			System.out.println("Lost connection");
		}
		while(true) {
			Message receivedMessage; 
			try {
				receivedMessage = (Message) ois.readObject();
				System.out.println("Received message " + receivedMessage.getMsg() + " CC " + receivedMessage.getContentCode());
				processObject(receivedMessage);
				
			} catch (IOException e) {
				System.out.println("Error reading message");
			}
		}
	}

	public void processObject(Message msg) {
		
		if(this.temporalOperaton != msg.getEventId()){	
				this.temporalOperaton = msg.getEventId();
		}
		
		switch( msg.getContentCode() ) {
			
			case 8:{	//Div
				if(msg.getSecond() != 0)
					{
						double result = divideController(msg.getFirst(), msg.getSecond());
						System.out.println("Divide.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
						Message resp = new Message(result, msg);
						resp.setEventId(this.temporalOperaton);
						forwardMessage(resp);
					}
				break;
				}
			
				case 13:{
						System.out.println("Got a request for div acknowledgement ");
						Message ackMessage = new Message();
						ackMessage.setContentCode(4);
						ackMessage.setEventId(this.fingerPrint);
						ackMessage.setFirst(this.numDiv);
						ackMessage.setMsg("d");
						forwardMessage(ackMessage);
						break;
					}
										
			case 14:{
				
				int tempCells = 0;
				
				switch(msg.getMsg()) {
				
					case "d":
					{
						tempCells = (int) this.numDiv;
						break;
					}
					default:
					{
						break;
					}
				}
				
				if(msg.getFirst() > this.numDiv)
				{
					for(int i = tempCells; i < msg.getFirst() - 1; i++) {
						File from = new File(this.file);
				        File to = Clonation.validateClone(this.file, this.rawFile);
				        try {
							Clonation.copy(from, to);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				this.numDiv = fileValidation.getNumServices(this.fileFldr);
				break;			
			}
				
			default:{
				break;
			}
		}
	}
	public static double divideController( double val1, double val2) {
        double ans = 0, num1 = val1, num2 = val2; 
		URL[] classLoaderUrls;
		try {
			classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Divide")};
			URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
	        Class<?> divClass = urlClassLoader.loadClass("Services.Divide");
	        Divide divFunction = (Divide)divClass.newInstance();
	        ans = divFunction.divideOperation(num1, num2);
	        System.out.println(ans);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}
    private static String getProcessId() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            return "";
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            // ignore
        }
        return "";

    }
	
}