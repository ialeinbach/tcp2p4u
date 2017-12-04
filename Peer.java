import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.HashSet;
import java.nio.file.Files;

public class Peer implements Runnable {
	private static final int REQ_PORT = 8888;
	private static final int COM_PORT = 40000;

	private boolean active;
	private int peerId;									// unique id for each Peer
	private HashSet<Message> msgHistory;				// store seen Messages to avoid infinite propogation
	private ArrayList<PeerHandler> peerHandlers;		// listen/speak to Peers
	private final Path chatFilepath;

	private SocketListener socketListener;				// listen for new Peers
	private EchoHandler echoHandler;					// broadcast incoming to all PeerHandlers
	private ServerSocket reqListener;					// listen for requests

	public Peer(int peerId) {
		this.peerId = peerId;
		this.peerHandlers = new ArrayList<PeerHandler>();
		this.msgHistory = new HashSet<Message>();
		this.active = false;
		this.chatFilepath = Paths.get(System.getProperty("user.home") + "/.peerchat.txt");
		this.resetChatHistory();

		this.echoHandler = new EchoHandler(this);
		this.socketListener = new SocketListener(this, COM_PORT);

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

	public void join(InetAddress addr) {
		try {
			Socket skt = new Socket(addr, COM_PORT);
			this.peerHandlers.add(new PeerHandler(this, skt));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void stop() {
		this.active = false;

		try {
			this.reqListener.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.socketListener.stop();

		for(PeerHandler ph : this.getPeerHandlers()) {
			ph.stop();
		}
	}

	public void broadcast(Message msg) {
		this.getEchoHandler().broadcast(msg);
	}

	public void handleRequest(Socket skt) {
		Request req = null;

		try {
			ObjectInputStream in = new ObjectInputStream(skt.getInputStream());
			req = (Request)in.readObject();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(req);

		if(req.getCommand().equals("stop")) {
			this.stop();
		} else if(req.getCommand().equals("broadcast")) {
			this.broadcast(new MsgMessage(String.join(";", req.getArguments()), this.getPeerId()));
		}
	}

	public static boolean isReqSocket(Socket skt) {
		String local = skt.getLocalSocketAddress().toString().split(":")[0];
		String remote = skt.getRemoteSocketAddress().toString().split(":")[0];

		return local.equals(remote);
	}

	public void resetChatHistory() {
		Path chatFilepath = this.getChatFilepath();

		try {
			Files.deleteIfExists(chatFilepath);
			Files.createFile(chatFilepath);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
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

	public Path getChatFilepath() {
		return this.chatFilepath;
	}

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Invalid arguments.");
			System.exit(1);
		}

		int peerId = Integer.parseInt(args[0]);
		Peer p = new Peer(peerId);

		if(peerId != 0) {
			try {
				InetAddress host = InetAddress.getByName("143.229.240.89");
				p.join(host);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		new Thread(p).start();
	}
}
