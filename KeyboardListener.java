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

		System.out.println("KeyboardListener started.");

		Scanner sc = new Scanner(System.in);
		String line = null;

		while(this.active) {
			try {
				System.out.println("[KeyboardListener] Waiting to read message...");
				line = sc.nextLine();	// does not include terminating '\n'
				System.out.println("[KeyboardListener] Read message from user.");

				this.setChanged();
				this.notifyObservers(new Message(line, this.peerId));
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
