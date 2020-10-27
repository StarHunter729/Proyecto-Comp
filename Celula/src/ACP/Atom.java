package ACP;

public class Atom {
	public static void main(String [] Args) {
		MotherCell handler = new MotherCell();
		handler.connectToDataField();
		handler.outputStream();
		handler.reportAliveStatus();
		try {
			handler.getMessage();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



/*System.out.println("Cell type: ");
@SuppressWarnings("resource")
Scanner reader = new Scanner(System.in);
char c = reader.next().trim().charAt(0);
switch(c) {
	case 's':{
		sumHandler handler = new sumHandler();
		handler.connectToDataField();
		handler.outputStream();
		handler.reportAliveStatus();
		try {
			handler.getMessage();
		} catch (ClassNotFoundException e) {
			
			//e.printStackTrace();
		}
		break;
	}
	
	case 'r':{
		subHandler handler = new subHandler();
		handler.connectToDataField();
		handler.outputStream();
		handler.reportAliveStatus();
		try {
			handler.getMessage();
		} catch (ClassNotFoundException e) {
			
			//e.printStackTrace();
		}
		break;
	}
	
	case 'm':{
		mulHandler handler = new mulHandler();
		handler.connectToDataField();
		handler.outputStream();
		handler.reportAliveStatus();
		try {
			handler.getMessage();
		} catch (ClassNotFoundException e) {
			
			//e.printStackTrace();
		}
		break;
	}
	
	case 'd':{
		divHandler handler = new divHandler();
		handler.connectToDataField();
		handler.outputStream();
		handler.reportAliveStatus();
		try {
			handler.getMessage();
		} catch (ClassNotFoundException e) {
			
			//e.printStackTrace();
		}
		break;
	}
}*/
