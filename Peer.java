import java.net.Socket;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.HashSet;

public class Peer implements Runnable {
	private static final int REQ_PORT = 8888;
	private static final int REQ_MAX_SIZE = 4096;

	private boolean active;
	private int peerId;									// unique id for each Peer
	private HashSet<Message> msgHistory;				// store seen Messages to avoid infinite propogation
	private ArrayList<PeerHandler> peerHandlers;		// listen/speak to Peers

	private SocketListener socketListener;				// listen for new Peers
	private EchoHandler echoHandler;					// broadcast incoming to all PeerHandlers
	private ServerSocket reqListener;					// listen for requests

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

	public void join(InetAddress addr, int port) {
		try {
			Socket skt = new Socket(addr, port);
			this.peerHandlers.add(new PeerHandler(this, skt));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void handleRequest(Socket skt) {
		byte[] content = new byte[REQ_MAX_SIZE];
		Request req = null;

		try {
			InputStream in = skt.getInputStream();
			in.read(content, 0, REQ_MAX_SIZE);
			req = new Request(new String(content));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(req.getCommand());
		String[] arguments = req.getArguments();
		for(String arg : arguments) {
			System.out.print("[" + arg + "]");
		}
		System.out.println();
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

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Invalid arguments.");
			System.exit(1);
		}

		int peerId = Integer.parseInt(args[0]);
		Peer p = new Peer(peerId, 40000);

		if(peerId != 0) {
			try {
				InetAddress host = InetAddress.getByName("143.229.240.89");
				p.join(host, 40000);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		new Thread(p).start();
	}
}
