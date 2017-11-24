import java.io.ObjectInputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PeerListener extends Observable implements Runnable {
	private ObjectInputStream input;
	private int peerId;
	private boolean active;

	public PeerListener(Peer peer, ObjectInputStream input, int peerId) {
		this.input = input;
		this.peerId = peerId;
		this.active = false;
	}

	// listen for Messages and send to holder Peer's msgQueue
	public void run() {
		this.active = true;

		while(this.active) {
			try {
				System.out.println("[PeerListener] Received message from a peer.");
				this.setChanged();
				this.notifyObservers((Message)this.input.readObject());
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
