import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observer;
import java.util.Observable;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.StandardOpenOption;
import java.net.InetAddress;

public class EchoHandler extends Observable implements Observer {
	private ArrayList<PeerHandler> peerHandlers;
	private HashSet<Message> msgHistory;
	private final Path chatFilepath;
	private int peerId;
	private Peer peer;

	public EchoHandler(Peer peer) {
		this.peerHandlers = peer.getPeerHandlers();
		this.msgHistory = peer.getMsgHistory();
		this.peerId = peer.getPeerId();
		this.chatFilepath = peer.getChatFilepath();
		this.peer = peer;
	}

	public void broadcast(Message msg) {
		int numPeers = this.peerHandlers.size();
		for(int i = 0; i < numPeers; i++) {
			this.peerHandlers.get(i).talk(msg);
		}
	}

	public Path getChatFilepath() {
		return this.chatFilepath;
	}

	public Peer getPeer() {
		return this.peer;
	}

	public void addToChat(MsgMessage msg) {
		byte[] msgBytes = (msg.toString() + "\n").getBytes(Charset.forName("UTF-8"));

		try {
			Files.createFile(this.chatFilepath);
		} catch(FileAlreadyExistsException faee) {
			// File exists... Not creating...
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			Files.write(this.getChatFilepath(), msgBytes, StandardOpenOption.APPEND);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void update(Observable obs, Object obj) {
		if(obj instanceof Message) {
			this.receive((Message)obj);
		}
	}

	public void enact(CtrlMessage msg) {
		InetAddress[] connections = msg.getConnections();
		for(InetAddress ip : connections) {
			this.getPeer().join(ip);
		}
	}

	public void receive(Message msg) {
		if(this.msgHistory.add(msg) && msg.getSender() != this.peerId) {
			if(msg instanceof MsgMessage) {
				this.addToChat((MsgMessage)msg);
				this.broadcast(msg);
			} else if(msg instanceof CtrlMessage) {
				this.enact((CtrlMessage)msg);
			}
		}
	}
}
