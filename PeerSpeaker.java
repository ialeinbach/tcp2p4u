import java.io.ObjectOutputStream;

public class PeerSpeaker {
	private ObjectOutputStream output;
	private int peerId;

	public PeerSpeaker(ObjectOutputStream output, int peerId) {
		this.output = output;
		this.peerId = peerId;
	}

	public void writeMessage(Message msg) {
		try {
			this.output.writeObject(msg);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
