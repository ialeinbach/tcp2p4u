import java.io.ObjectOutputStream;

public class PeerSpeaker {
	private final ObjectOutputStream output;
	private final PeerHandler peerHandler;

	public PeerSpeaker(PeerHandler peerHandler, ObjectOutputStream output) {
		this.peerHandler = peerHandler;
		this.output = output;
	}

	public void stop() {
		peerHandler.stop();
	}

	public void writeMessage(Message msg) {
		try {
			output.writeObject(msg);
		} catch(NullPointerException npe) {
			System.out.println("Remote Peer left unexpectedly.");
			stop();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
