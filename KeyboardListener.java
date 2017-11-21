import java.util.concurrent.SynchronousQueue;
import java.util.Scanner;

public class KeyboardListener implements Runnable {
	SynchronousQueue<Message> msgQueue;
	int peerId;
	boolean active;

	public KeyboardListener(Peer peer) {
		this.msgQueue = peer.getMsgQueue();
		this.peerId = peer.getPeerId();
		this.active = false;
	}

	public void run() {
		this.active = true;

		System.out.println("KeyboardListener started.");

		Scanner sc = new Scanner(System.in);
		String line = null;

		while(this.active) {
			System.out.println("[KeyboardListener] Waiting to read message...");
			line = sc.nextLine();	// does not include terminating '\n'
			System.out.println("[KeyboardListener] Read message from user.");

			try {
				this.msgQueue.put(new Message(line, this.peerId));
			} catch(InterruptedException ie) {
				ie.printStackTrace();
				System.exit(1);
			}

			System.out.println("[KeyboardListener] Added message to msgQueue.");
		}
	}
}
