import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Peer implements Runnable {
	private static final int REQ_PORT = 8888;
	private static final int COM_PORT = 40000;
	private static final String chatFilename = ".peerchat.txt";

	private boolean active;
	private final int peerId;								// unique id for each Peer

	private final HashSet<Message> messageHistory;				// detect duplicate Messages to avoid infinite propogation
	private final ArrayList<PeerHandler> peerHandlers;		// track adjacent Peers
	private final Path chatFilepath;

	private final SocketListener socketListener;			// listen for new Peers
	private final EchoHandler echoHandler;					// broadcast to all PeerHandlers
	private final ServerSocket requestListener;				// listen for Requests

	public Peer(int peerId) {
		active = false;
		this.peerId = peerId;

		messageHistory = new HashSet<Message>();
		peerHandlers = new ArrayList<PeerHandler>();
		chatFilepath = Paths.get(System.getProperty("user.home") + "/" + Peer.chatFilename);
		resetChatHistory();

		ServerSocket ss = null;

		try {
			ss = new ServerSocket(REQ_PORT);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		requestListener = ss;
		echoHandler = new EchoHandler(this);
		socketListener = new SocketListener(this, COM_PORT);
	}

	public void run() {
		active = true;
		new Thread(getSocketListener()).start();

		while(active) {
			try {
				Socket request = getRequestListener().accept();
				handleRequest(request);
				request.close();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void join(InetAddress addrRemotePeer) {
		try {
			Socket sktRemotePeer = new Socket(addrRemotePeer, COM_PORT);
			getPeerHandlers().add(new PeerHandler(this, sktRemotePeer));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void stop() {
		active = false;

		try {
			getRequestListener().close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		getSocketListener().stop();
		getEchoHandler().broadcastExit();

		for(PeerHandler ph : getPeerHandlers()) {
			ph.stopAll();
		}
	}

	public void handleRequest(Socket socket) {
		if(!isRequestSocket(socket)) { return; }

		Request request = null;

		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			request = (Request)in.readObject();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(request);

		if(request.getCommand().equals("stop")) {
			stop();
		} else if(request.getCommand().equals("broadcast")) {
			getEchoHandler().broadcast(new MsgMessage(String.join(";", request.getArguments()), getPeerId()));
		} else if(request.getCommand().equals("info")) {
			System.out.println(info());
		}
	}

	public static boolean isRequestSocket(Socket requestSocket) {
		String local = requestSocket.getLocalSocketAddress().toString().split(":")[0];
		String remote = requestSocket.getRemoteSocketAddress().toString().split(":")[0];

		return local.equals(remote);
	}

	public void resetChatHistory() {
		Path chatFilepath = getChatFilepath();

		try {
			Files.deleteIfExists(chatFilepath);
			Files.createFile(chatFilepath);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String info() {
		return  String.format("[]==============[]\n" +
							  "|| peerId: %2d   ||\n" +
							  "|| numPeers: %2d ||\n" +
							  "[]==============[]\n", getPeerId(), getPeerHandlers().size());
	}

	public boolean status() {
		return active;
	}

	public int getPeerId() {
		return peerId;
	}

	public HashSet<Message> getMessageHistory() {
		return messageHistory;
	}

	public ArrayList<PeerHandler> getPeerHandlers() {
		return peerHandlers;
	}

	public Path getChatFilepath() {
		return chatFilepath;
	}

	public SocketListener getSocketListener() {
		return socketListener;
	}

	public EchoHandler getEchoHandler() {
		return echoHandler;
	}

	public ServerSocket getRequestListener() {
		return requestListener;
	}

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Invalid arguments.");
			System.exit(1);
		}

		int peerId = Integer.parseInt(args[0]);
		Peer peer = new Peer(peerId);

		if(peerId != 0) {
			try {
				InetAddress ianLaptop = InetAddress.getByName("143.229.240.89");
				peer.join(ianLaptop);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		new Thread(peer).start();
	}
}
