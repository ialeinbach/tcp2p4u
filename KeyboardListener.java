import java.util.concurrent.SynchronousQueue;
import java.util.Scanner;
import java.util.Observable;

public class KeyboardListener extends Observable implements Runnable {
	int peerId;
	boolean active;

	public KeyboardListener(Peer peer) {
		this.addObserver(peer.getEchoHandler());
		this.peerId = peer.getPeerId();
		this.active = false;
	}

	public void run() {
		this.active = true;
		Scanner sc = new Scanner(System.in);
		String line = null;

		while(this.active) {
			try {
				line = sc.nextLine();	// does not include terminating '\n'
				this.setChanged();
				this.notifyObservers(new MsgMessage(line, this.peerId));
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
