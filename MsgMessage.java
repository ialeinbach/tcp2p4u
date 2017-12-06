public class MsgMessage extends Message {
	private String content;

	public MsgMessage(String content, int sender) {
		super(sender);
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public String toString() {
		return "[Message from Peer " + this.getSender() + "] " + this.getMessage();
	}
}
