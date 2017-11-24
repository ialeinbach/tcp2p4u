public class MsgMessage extends Message {
	public MsgMessage(String message, int sender) {
		super(message, sender);
	}

	public String getMessage() {
		return super.getContent();
	}

	public String toString() {
		return "[Message from Peer " + super.getSender() + "] " + this.getMessage();
	}
}
