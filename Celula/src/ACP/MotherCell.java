package ACP;

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
import Services.Add;
import Services.Divide;
import Services.Multiply;
import Services.Substract;
import fileHandler.Clonation;
import fileHandler.fileValidation;

public class MotherCell {
	
	//Thread Variables
	int Min = 5000;
	int Max = 5100;
	int localPort;
	Socket localSocket;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	//Cell Variables
	String fingerPrint;
	String temporalOperaton = "";
	double numDiv = 0, numSum = 0, numSub = 0, numMul = 0;
	boolean sumEnabled = false, subEnabled = false, mulEnabled = false, divEnabled = false;   
	
	//Services Operations
	String fileFldr = "";
	String file = "";
	String rawFile = "";
	
	
	//Service Specific Strings Add
	
	String fileFldrAdd = "C:/temp/Services/Add";
	String fileAdd = "C:/temp/Services/Add/Add.jar";
	String rawFileAdd = "C:/temp/Services/Add/Add-Clone";

	//Service Specific Strings Sub
	
	String fileFldrSub = "C:/temp/Services/Substract";
	String fileSub = "C:/temp/Services/Substract/Substract.jar";
	String rawFileSub = "C:/temp/Services/Substract/Substract-Clone";
	
	//Service Specific Strings Mul
	
	String fileFldrMul = "C:/temp/Services/Multiply";
	String fileMul = "C:/temp/Services/Multiply/Multiply.jar";
	String rawFileMul = "C:/temp/Services/Multiply/Multiply-Clone";
	
	//Service Specific Strings Div
	
	String fileFldrDiv = "C:/temp/Services/Divide";
	String fileDiv = "C:/temp/Services/Divide/Divide.jar";
	String rawFileDiv = "C:/temp/Services/Divide/Divide-Clone";
	
	//Constructor
	
	public MotherCell() {
		this.fingerPrint = getProcessId();
		this.numSum = fileValidation.getNumServices(this.fileFldrAdd);
		this.numSub = fileValidation.getNumServices(this.fileFldrSub);
		this.numMul = fileValidation.getNumServices(this.fileFldrMul);
		this.numDiv = fileValidation.getNumServices(this.fileFldrDiv);
		System.out.println("Empty Cell instanced");
	} 
	
	//Thread Functions
	
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

	public void processObject(Message msg) {
		
		if(this.temporalOperaton != msg.getEventId()){	
				this.temporalOperaton = msg.getEventId();
		}
		System.out.println("Received message " + msg.getMsg() + "CC " + msg.getContentCode());
		
		switch( msg.getContentCode() ) {
			
			case 5:{	//Add
				if(this.sumEnabled) {
					double result = addController(msg.getFirst(), msg.getSecond());
					System.out.println("Add.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
					Message resp = new Message(result, msg);
					resp.setEventId(this.temporalOperaton);
					forwardMessage(resp);
				}
				else {
					System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
				}
				break;
				
			}
			
			case 6:{	//Sub
				if(this.subEnabled) {
					double result = substractionController(msg.getFirst(), msg.getSecond());
					System.out.println("Substract.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
					Message resp = new Message(result, msg);
					resp.setEventId(this.temporalOperaton);
					forwardMessage(resp);
				}
				else {
					System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
				}
				break;
			}
			
			case 7:{	//Mul
				if(this.mulEnabled) {
					double result = multiplyController(msg.getFirst(), msg.getSecond());
					System.out.println("Multiply.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
					Message resp = new Message(result, msg);
					resp.setEventId(this.temporalOperaton);
					forwardMessage(resp);
				}
				else {
					System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
				}
				break;
			}
			
			case 8:{	//Div
				if(this.divEnabled) {
					if(msg.getSecond() != 0)
					{
						double result = divideController(msg.getFirst(), msg.getSecond());
						System.out.println("Divide.jar has received the parameters: " + msg.getFirst() + " "+ msg.getSecond());
						Message resp = new Message(result, msg);
						resp.setEventId(this.temporalOperaton);
						forwardMessage(resp);
					}
				}
				else {
					System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
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
				char requestType = 'e';
				switch(msg.getMsg()) {
					
					case "s":
					{
						tempCells = (int) this.numSum;
						this.file = this.fileAdd; 
						this.rawFile = this.rawFileAdd;
						this.fileFldr = this.fileFldrAdd;
						break;
					}
					case "r":
					{
						tempCells = (int) this.numSub;
						this.file = this.fileSub; 
						this.rawFile = this.rawFileSub;
						this.fileFldr = this.fileFldrSub;
						break;
					}
					case "m":
					{
						tempCells = (int) this.numMul;
						this.file = this.fileMul; 
						this.rawFile = this.rawFileMul;
						this.fileFldr = this.fileFldrMul;
						break;
					}
					case "d":
					{
						tempCells = (int) this.numDiv;
						this.file = this.fileDiv; 
						this.rawFile = this.rawFileDiv;
						this.fileFldr = this.fileFldrDiv;
						break;
					}

					default:
					{
						break;
					}
				}
				
				/*if(((requestType == 's') && (sumEnabled = true) )||((requestType == 'r') && (subEnabled = true) )
						|| ( (requestType == 'm') && (mulEnabled = true) ) || ( (requestType == 'd') && (divEnabled = true) )) {
					System.out.println(x);*/
					if(msg.getFirst() > tempCells) {
						for(int i = tempCells; i < msg.getFirst(); i++) {
							File from = new File(this.file);
					        File to = Clonation.validateClone(this.file, this.rawFile);
					        try {
								Clonation.copy(from, to);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					//}
					
					this.numSum = fileValidation.getNumServices(this.fileFldrAdd);
					this.numSub = fileValidation.getNumServices(this.fileFldrSub);
					this.numMul = fileValidation.getNumServices(this.fileFldrMul);
					this.numDiv = fileValidation.getNumServices(this.fileFldrDiv);
					break;
				}
				break;
			}
			
			case 15:{ 
				char[] msgArray = msg.getMsg().toCharArray();
				
				if(msgArray[0] == '1') {
					this.sumEnabled = true;
					System.out.println("Addition has been enabled for the cell " + this.fingerPrint);
				}
				else {
					this.sumEnabled = false;
					System.out.println("Addition has been disabled for the cell " + this.fingerPrint);
				}
				
				if(msgArray[1] == '1') {
					this.subEnabled = true;
				}
				else {
					this.subEnabled = false;
					System.out.println("Substraction has been disabled for the cell " + this.fingerPrint);
				}
				
				if(msgArray[2] == '1') {
					this.mulEnabled = true;
					System.out.println("Multiplication has been enabled for the cell " + this.fingerPrint);
				}
				else {
					this.mulEnabled = false;
					System.out.println("Multiplication has been disabled for the cell " + this.fingerPrint);
				}
				
				if(msgArray[3] == '1') {
					this.divEnabled = true;
				}
				else {
					this.divEnabled = false;
					System.out.println("Divition has been disabled for the cell " + this.fingerPrint);
				}
				
				break;
			}
			
			default:{
				break;
			}
		}
	}

	
	//Service Functions
	
	@SuppressWarnings("resource")
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
	
	@SuppressWarnings("resource")
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
	
	@SuppressWarnings("resource")
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
	
	@SuppressWarnings("resource")
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

	
	//Cell Functions
	
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
