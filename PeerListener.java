import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

public class PeerListener extends Thread {
	private SynchronousQueue<Message> msgQueue;	// same for all PeerHandlers per Peer
	private ObjectInputStream input;
	private int peerId;
	private boolean active;

	public PeerListener(ObjectInputStream input, SynchronousQueue<Message> msgQueue, int peerId) {
		this.msgQueue = msgQueue;
		this.input = input;
		this.peerId = peerId;
		this.active = true;
	}

	// listen for Messages and send to holder Peer's msgQueue
	public void run() {
		Message incoming = null;

		while(this.active) {
			try {
				incoming = (Message)this.input.readObject();		// blocking
				System.out.println("[PeerListener] Received message from a peer.");
				this.msgQueue.put(incoming);
			} catch(IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			} catch(ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				System.exit(1);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
				System.exit(1);
			}
		}
	}
}
