import java.net.Socket;
import java.util.Arrays;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.concurrent.SynchronousQueue;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.net.InetAddress;
import java.io.IOException;
import java.util.HashSet;

public class Peer implements Runnable {
	private static final int REQ_PORT = 8888;

	private int peerId;									// unique id for each Peer object
	private ArrayList<PeerHandler> peerHandlers;		// listen/speak to peers
	private HashSet<Message> msgHistory;
	private ServerSocket reqListener;
	private int nextPeerId;
	private boolean active;

	private SocketListener socketListener;				// listen for new peers
	private EchoHandler echoHandler;					// broadcast incoming to all peerhandlers
	private KeyboardListener kbListener;				// take input from keyboard

	public Peer(int peerId, int port) {
		this.peerId = peerId;
		this.peerHandlers = new ArrayList<PeerHandler>();
		this.msgHistory = new HashSet<Message>();
		this.nextPeerId = this.peerId + 1;
		this.active = false;

		this.echoHandler = new EchoHandler(this);
		this.kbListener = new KeyboardListener(this);
		this.socketListener = new SocketListener(this, port);

		try {
			this.reqListener = new ServerSocket(REQ_PORT);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		this.active = true;
		new Thread(this.socketListener).start();
		new Thread(this.kbListener).start();

		int nextPeer = 1;		// stand-in for assigning IDs to new peers
		Socket skt = null;

		while(this.active) {
			try {
				Socket req = this.reqListener.accept();

				if(isReqSocket(req)) {
					handleRequest(req);
				}

				req.close();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void handleRequest(Socket req) {
		try {
			InputStream in = req.getInputStream();
			byte[] content = new byte[4096];
			in.read(content);
			System.out.println(Arrays.toString(content));
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	public static boolean isReqSocket(Socket skt) {
		String local = skt.getLocalSocketAddress().toString().split(":")[0];
		String remote = skt.getRemoteSocketAddress().toString().split(":")[0];

		return local.equals(remote);
	}

	public int getNextPeerId() {
		return this.nextPeerId++;
	}

	public int getPeerId() {
		return this.peerId;
	}

	public ArrayList<PeerHandler> getPeerHandlers() {
		return this.peerHandlers;
	}

	public EchoHandler getEchoHandler() {
		return this.echoHandler;
	}

	public HashSet<Message> getMsgHistory() {
		return this.msgHistory;
	}

	// call once initially to look for existing peers
	public void join(InetAddress ipAddr, int port) {
		try {
			Socket skt = new Socket(ipAddr, port);
			this.peerHandlers.add(new PeerHandler(this, skt));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
