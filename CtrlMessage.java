import java.net.InetAddress;

public class CtrlMessage extends Message {
	public CtrlMessage(String ins, int sender) {
		super(ins, sender);
	}

	public String[] getConnections() {
		return super.getContent().split(",");
	}

	public String toString() {
		return "[Peer " + this.sender + " disconnecting] Connect to: " + this.getContent();
	}
}
