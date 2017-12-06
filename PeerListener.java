import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.net.SocketException;
import java.io.EOFException;

public class PeerListener extends Observable implements Runnable {
	private ObjectInputStream input;
	private boolean active;

	public PeerListener(Peer peer, ObjectInputStream input) {
		this.input = input;
		this.active = false;
	}

	public boolean status() {
		return active;
	}

	public void stop() {
		active = false;
		setChanged();
		notifyObservers();
	}

	public void run() {
		this.active = true;

		while(this.active) {
			try {
				this.setChanged();
				this.notifyObservers((Message)this.input.readObject());
			} catch(SocketException sce) {
				// Local Peer stopping...
				stop();
			} catch(EOFException eofe) {
				// Remote Peer left...
				stop();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
