import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.lang.InterruptedException;
import java.lang.management.ManagementFactory;
import java.util.HashSet;

public class EchoHandler extends Thread {
	private SynchronousQueue<Message> msgQueue;
	private ArrayList<PeerHandler> peerHandlers;
	private HashSet<Message> msgHistory;
	private int peerId;
	private boolean active;

	public EchoHandler(Peer peer) {
		this.msgQueue = peer.getMsgQueue();
		this.msgHistory = peer.getMsgHistory();
		this.peerHandlers = peer.getPeerHandlers();
		this.peerId = peer.getPeerId();
		this.active = false;
	}

	public void run() {
		System.out.println("EchoHandler started.");

		this.active = true;
		Message msg = null;

		while(this.active) {
			try {
				msg = this.msgQueue.take();	// blocking
				System.out.println("[EchoHandler] Took message from msgQueue.");
			} catch(InterruptedException ie) {
				ie.printStackTrace();
				System.exit(1);
			}

			if(this.msgHistory.add(msg)) {
				System.out.println("[EchoHandler] Unique message found.");

				if(msg.getSender() == this.peerId) {
					System.out.println("[EchoHandler] Identified self as sender.");
				} else {
					System.out.println("[Some spacing]                                          [Message] " + msg);
				}


				for(int i = 0; i < this.peerHandlers.size(); i++) {
					this.peerHandlers.get(i).talk(msg);
				}

				System.out.println("[EchoHandler] Broadcasted message.");
			} else {
				System.out.println("[EchoHandler] Redundant message found. Ignored.");
			}

		}
	}
}
