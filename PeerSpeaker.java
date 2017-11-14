import java.io.ObjectOutputStream;
import java.io.IOException;

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
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

		System.out.println("[PeerSpeaker] Wrote message to socket.");
	}
}
