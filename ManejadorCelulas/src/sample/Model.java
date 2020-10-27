package sample;

import Objects.Message;

public class Model {
	public Message createRequest(char type, int requestedCells) {
		Message msg = new Message();
		switch(type) {
			case 's':{
				msg.setFirst(requestedCells);
				msg.setContentCode(15);
				System.out.println("Created request for sums");
				break;
			}
			
			case 'r':{
				msg.setFirst(requestedCells);
				msg.setContentCode(16);
				System.out.println("Created request for subs");
				break;
			}
			
			case 'm':{
				msg.setFirst(requestedCells);
				msg.setContentCode(17);
				System.out.println("Created request for mults");
				break;
			}
			
			case 'd':{
				msg.setFirst(requestedCells);
				msg.setContentCode(18);
				System.out.println("Created request for divs");
				break;
			}
			default:{
				break;
			}
		}
		return msg;
	}
}

