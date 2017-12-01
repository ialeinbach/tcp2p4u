import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PeerListener extends Observable implements Runnable {
	private ObjectInputStream input;
	private boolean active;

	public PeerListener(Peer peer, ObjectInputStream input) {
		this.input = input;
		this.active = false;
	}

	public void stop() {
		this.active = false;
	}

	public boolean status() {
		return this.active;
	}

	public void run() {
		this.active = true;

		while(this.active) {
			try {
				this.setChanged();
				this.notifyObservers((Message)this.input.readObject());
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
