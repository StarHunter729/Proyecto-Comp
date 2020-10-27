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
			case -1:{
				this.msg = "You were added to the connections table ";
				this.contentCode = -1;
				break;
			}
		}
	}
	public Message(int contentCode, double first, double second, String sign) {
		
		this.eventId = String.valueOf(uniqueCurrentTimeMS());
		this.contentCode = contentCode;
		this.first = first;
		this.second = second;
		this.msg = sign;
		this.original = true;
		this.isAcknowledgement = false;
	}
	
	public Message( String eventId ) {
		
		this.eventId = eventId;
		this.isAcknowledgement = true;
		
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
}
