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

public class EchoHandler extends Observable implements Observer {
	private ArrayList<PeerHandler> peerHandlers;
	private HashSet<Message> msgHistory;
	private final Path chatFilepath;
	private int peerId;

	public EchoHandler(Peer peer) {
		this.peerHandlers = peer.getPeerHandlers();
		this.msgHistory = peer.getMsgHistory();
		this.peerId = peer.getPeerId();
		this.chatFilepath = peer.getChatFilepath();
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
		if(obj instanceof MsgMessage) {
			MsgMessage msg = (MsgMessage)obj;
			this.receive(msg);
		} else if(obj instanceof CtrlMessage) {
			CtrlMessage msg = (CtrlMessage)obj;
			this.receive(msg);
		}
	}

	public boolean receive(Message msg) {
		if(this.msgHistory.add(msg)) {
			if(msg.getSender() != this.peerId) {
				this.addToChat((MsgMessage)msg);
			}

			this.broadcast(msg);
			return true;
		}

		return false;
	}
}
