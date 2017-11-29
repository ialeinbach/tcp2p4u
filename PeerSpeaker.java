import java.io.ObjectOutputStream;

public class PeerSpeaker {
	private ObjectOutputStream output;

	public PeerSpeaker(ObjectOutputStream output) {
		this.output = output;
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
