package Objects;

public class Connection {
	private String id;
	private long lastKnownAcknowledgement;
	private char ackType;
	private String cellFingerprint;
	public Connection(String id)
	{
		this.id = id;
		this.lastKnownAcknowledgement = System.currentTimeMillis();
	}
	public String getId() { return this.id; }
	public long getlastAck() { return this.lastKnownAcknowledgement; }
	public void setId(String id) { this.id = id;}
	public void setLastKnown() {this.lastKnownAcknowledgement = System.currentTimeMillis();}
}
