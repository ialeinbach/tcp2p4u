public class MsgMessage extends Message {
	public MsgMessage(String message, int sender) {
		super(message, sender);
	}

	public String getMessage() {
		return this.getContent();
	}

	public String toString() {
		return "[Message from Peer " + this.getSender() + "] " + this.getMessage();
	}
}
