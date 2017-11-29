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
	private boolean active;

	private SocketListener socketListener;				// listen for new peers
	private EchoHandler echoHandler;					// broadcast incoming to all peerhandlers

	public Peer(int peerId, int port) {
		this.peerId = peerId;
		this.peerHandlers = new ArrayList<PeerHandler>();
		this.msgHistory = new HashSet<Message>();
		this.active = false;

		this.echoHandler = new EchoHandler(this);
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
			System.out.println(new String(content));
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

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Invalid arguments.");
			System.exit(1);
		}

		int peerId = Integer.parseInt(args[0]);
		Peer p = new Peer(peerId, 40000);

		byte[] addr = {(byte)143, (byte)229, (byte)240, (byte)89};

		if(peerId != 0) {
			try {
				InetAddress host = InetAddress.getByAddress(addr);
				p.join(host, 40000);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		new Thread(p).start();
	}
}
