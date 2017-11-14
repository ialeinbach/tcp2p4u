import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.net.InetAddress;
import java.io.IOException;
import java.util.HashSet;

public class Peer extends Thread {
	private int peerId;									// unique id for each Peer object
	private ArrayList<PeerHandler> peerHandlers;		// listen/speak to peers
	private SynchronousQueue<Message> msgQueue;			// serialize incoming messages to broadcast
	private HashSet<Message> msgHistory;
	private boolean active;

	private SocketListener socketListener;				// listen for new peers
	private EchoHandler echoHandler;					// broadcast incoming to all peerhandlers
	private KeyboardListener kbListener;				// take input from keyboard

	public Peer(int peerId, int port) {
		this.peerId = peerId;
		this.peerHandlers = new ArrayList<PeerHandler>();
		this.msgQueue = new SynchronousQueue<Message>();
		this.msgHistory = new HashSet<Message>();
		this.active = false;

		this.echoHandler = new EchoHandler(this);
		this.kbListener = new KeyboardListener(this);
		this.socketListener = new SocketListener(this, port);
	}

	public void run() {
		this.active = true;

		System.out.println("Peer started.");

		this.socketListener.start();
		this.echoHandler.start();
		this.kbListener.start();

		int nextPeer = 1;		// stand-in for assigning IDs to new peers
		Socket skt = null;


		// Lord please forgive me for this abomination
		Object obj = new Object();
		synchronized(obj) {
			try {
				obj.wait();
			} catch(InterruptedException ie) {
				System.out.println("[Peer] Interrupted while idle.");
				ie.printStackTrace();
				System.exit(1);
			}
		}
	}

	public int getPeerId() {
		return this.peerId;
	}

	public ArrayList<PeerHandler> getPeerHandlers() {
		return this.peerHandlers;
	}

	public SynchronousQueue<Message> getMsgQueue() {
		return this.msgQueue;
	}

	public HashSet<Message> getMsgHistory() {
		return this.msgHistory;
	}

/*	--> TO DO <--
 *	public int extractPeerId(Socket skt);
 */

	// call once initially to look for existing peers
	public void join(InetAddress ipAddr, int port) {
		Socket skt = null;

		try {
			skt = new Socket(ipAddr, port);
			System.out.println("[Peer] Bootstrapping socket to " + ipAddr.getHostAddress() + ":" + port + "...");

			PeerHandler ph = new PeerHandler(skt, this.msgQueue, this.peerId);
			ph.listen();
			this.peerHandlers.add(ph);
			System.out.println("[Peer] Succesfully bootstrapped.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		} catch(IllegalStateException ise) {
			ise.printStackTrace();
			System.exit(1);
		}

		System.out.println("[Peer] Successfully found a peer.");
	}
}
