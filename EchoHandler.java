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
	private final Peer peer;

	public EchoHandler(Peer peer) {
		this.peer = peer;
	}

	public void broadcast(Message msg) {
		for(PeerHandler ph : peer.getPeerHandlers()) {
			ph.talk(msg);
		}
	}

	public void broadcastExit() {
		ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
		int numPeers = peerHandlers.size();
		InetAddress nextPeer;

		for(int i = 1; i < numPeers; i++) {
			nextPeer = peerHandlers.get(i).getRemoteAddress();
			peerHandlers.get(i - 1).talk(new CtrlMessage(nextPeer, peer.getPeerId()));
		}
	}

	public void addToChat(MsgMessage msg) {
		byte[] msgBytes = (msg.toString() + "\n").getBytes(Charset.forName("UTF-8"));

		try {
			Files.createFile(peer.getChatFilepath());
		} catch(FileAlreadyExistsException faee) {
			// File exists... Not creating...
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			Files.write(peer.getChatFilepath(), msgBytes, StandardOpenOption.APPEND);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void update(Observable obs, Object obj) {
		if(obs instanceof PeerListener && obj instanceof Message) {
			receive((Message)obj);
		}
	}

	public void enact(CtrlMessage msg) {
		peer.join(msg.getConnection());
	}

	public void receive(Message msg) {
		if(peer.getMsgHistory().add(msg) && msg.getSender() != peer.getPeerId()) {
			if(msg instanceof MsgMessage) {
				addToChat((MsgMessage)msg);
				broadcast(msg);
			} else if(msg instanceof CtrlMessage) {
				enact((CtrlMessage)msg);
			}
		}
	}
}
