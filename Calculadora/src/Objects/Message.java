package Objects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Message implements Serializable{

	private static final long serialVersionUID = 7350392272249743103L;
	private static final AtomicLong identifier = new AtomicLong();
	
	private String eventId;
	private String msg;
	private int contentCode;
	private double first, second;
	private boolean original;
	private String idOriginalSender;
	private boolean isAcknowledgement;
	
	public Message() {}
	public Message( int firstContactCase ) {
		this.isAcknowledgement = false;
		this.msg = "First Contact";
		switch(firstContactCase) {
			case 1:{
				this.msg = "Connection From Node";
				this.contentCode = 1;
				break;
			}
			case 2:{
				this.msg = "Connection From Interface";
				this.contentCode = 2;
				break;
			}
			case 3:{
				this.msg = "Connection From Cell";
				this.contentCode = 3;
				break;
			}
		}
	}
	
	public Message(int contentCode, double first, double second, String msg) {
		
		this.eventId = String.valueOf(uniqueCurrentTimeMS());
		this.contentCode = contentCode;
		this.first = first;
		this.second = second;
		this.msg = "Operation " + first + " " + msg + " " + second;
		this.original = true;
		this.isAcknowledgement = false;
	}
	
	public Message( int cc , int i) {
		switch(i) {
			case 0:{
				this.contentCode = cc;
			}
			case 1:{
				this.isAcknowledgement = true;
			}
		}		
	}
	
	public Message(double result, Message msg) {
		
		this.eventId = msg.getEventId();
		this.contentCode = 9;
		this.idOriginalSender = msg.idOriginalSender;
		this.original = true;
		this.msg = Double.toString(result);
		
	}
	
	public static long uniqueCurrentTimeMS() {
	    long now = System.currentTimeMillis();
	    while(true) {
	        long lastTime = identifier.get();
	        if (lastTime >= now)
	            now = lastTime+1;
	        if (identifier.compareAndSet(lastTime, now))
	            return now;
	    }
	}
	public String getMsg() {
		return this.msg;
	}
	public int getContentCode() {
		return this.contentCode;
	}
	public double getFirst() {
		return this.first;
	}
	
	public void setFirst( double s) {
		this.first = s;
	}
	
	public double getSecond() {
		return this.second;
	}
	public boolean getOriginal() {
		return this.original;
	}
	public String getEventId() {
		return this.eventId;
	}
	public String getOriginalSender() {
		return this.idOriginalSender;
	}
	public boolean getIsAcknowledgement() {
		return this.isAcknowledgement;
	}
	public void updateOriginal() {
		this.original = false;
	}
	public void setContentCode(int i) {
		this.contentCode = i;
	}
	public void setMessage(String s) {
		this.msg = s;
	}
}
