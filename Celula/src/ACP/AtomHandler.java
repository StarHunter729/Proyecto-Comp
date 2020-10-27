package ACP;

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
import Services.Add;
import Services.Divide;
import Services.Multiply;
import Services.Substract;

public class AtomHandler {
	
	int Min = 5000;
	int Max = 5010;
	int localPort;
	Socket localSocket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String fingerPrint;
	String temporalOperaton = "";
	double numSum = 1, numSub = 1, numDiv = 1, numMul = 1;
	
	public AtomHandler() {
		this.fingerPrint = getProcessId();
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
				System.out.println("Received message " + receivedMessage.getMsg() + "CC " + receivedMessage.getContentCode());
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
			
			case 5:{	//Add
				double result = addController(msg.getFirst(), msg.getSecond());
				System.out.println("Add.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
				Message resp = new Message(result, msg);
				resp.setEventId(this.temporalOperaton);
				forwardMessage(resp);
				break;
			}
			
			case 6:{	//Sub
				double result = substractionController(msg.getFirst(), msg.getSecond());
				System.out.println("Substract.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
				Message resp = new Message(result, msg);
				resp.setEventId(this.temporalOperaton);
				forwardMessage(resp);
				break;
			}
			
			case 7:{	//Mul
				double result = multiplyController(msg.getFirst(), msg.getSecond());
				System.out.println("Multiply.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
				Message resp = new Message(result, msg);
				resp.setEventId(this.temporalOperaton);
				forwardMessage(resp);
				break;
			}
			
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
			
			case 10:{
						System.out.println("Got a request for sum acknowledgement ");
						Message ackMessage = new Message();
						ackMessage.setContentCode(4);
						ackMessage.setEventId(this.fingerPrint);
						ackMessage.setFirst(this.numSum);
						ackMessage.setMsg("s");
						forwardMessage(ackMessage);
						break;
					}
			
			case 11:{	
						System.out.println("Got a request for sub acknowledgement ");
						Message ackMessage = new Message();
						ackMessage.setContentCode(4);
						ackMessage.setEventId(this.fingerPrint);
						ackMessage.setFirst(this.numSub);
						ackMessage.setMsg("r");
						forwardMessage(ackMessage);
						break;
					}
						
			case 12:{
						System.out.println("Got a request for mul acknowledgement ");
						Message ackMessage = new Message();
						ackMessage.setContentCode(4);
						ackMessage.setEventId(this.fingerPrint);
						ackMessage.setFirst(this.numMul);
						ackMessage.setMsg("m");
						forwardMessage(ackMessage);
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
					case "s":
						{
							tempCells = (int) this.numSum;
							break;
						}
					case "r":
					{
						tempCells = (int) this.numSub;
						break;
					}
					case "m":
					{
						tempCells = (int) this.numMul;
						break;
					}
					case "d":
					{
						tempCells = (int) this.numDiv;
						break;
					}
				}
				
				for(int i = tempCells; i < msg.getFirst(); i++) {
					Process ps;
					try {
						ps = Runtime.getRuntime().exec(new String[]{"java","-jar","C:\\temp\\Providers\\Clone.jar"});
				        ps.waitFor();
				        java.io.InputStream is = ps.getInputStream();
				        byte b[] = new byte[is.available()];
				        is.read(b,0,b.length);
				        System.out.println(new String(b));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				break;			
			}
				
			default:{
				break;
			}
		}
	}
	public static double addController( double val1, double val2) {
        double ans = 0, num1 = val1, num2 = val2; 
		URL[] classLoaderUrls;
		try {
			classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Add")};
			URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
	        Class<?> addClass = urlClassLoader.loadClass("Services.Add");
	        Add addFunction = (Add)addClass.newInstance();
	        ans = addFunction.addOperation(num1, num2);
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
	public static double substractionController( double val1, double val2) {
        double ans = 0, num1 = val1, num2 = val2; 
		URL[] classLoaderUrls;
		try {
			classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Substract")};
			URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
	        Class<?> subClass = urlClassLoader.loadClass("Services.Substract");
	        Substract subFunction = (Substract)subClass.newInstance();
	        ans = subFunction.substractOperation(num1, num2);
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
	public static double multiplyController( double val1, double val2) {
        double ans = 0, num1 = val1, num2 = val2; 
		URL[] classLoaderUrls;
		try {
			classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Multiply")};
			URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
	        Class<?> mulClass = urlClassLoader.loadClass("Services.Multiply");
	        Multiply mulFunction = (Multiply)mulClass.newInstance();
	        ans = mulFunction.multiplyOperation(num1, num2);
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
