import java.net.InetAddress;

public class CtrlMessage extends Message {
	private InetAddress[] connections;

	public CtrlMessage(InetAddress[] connections, int sender) {
		super(sender);
		this.connections = connections;
	}

	public InetAddress[] getConnections() {
		return this.connections;
	}

	public String toString() {
		return "[Peer " + this.getSender() + " disconnecting] Connect to: " + this.getConnections();
	}
}
