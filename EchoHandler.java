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

	public void update(Observable obs, Object obj) {
		if(obs instanceof PeerListener && obj instanceof Message) {
			receive((Message)obj);
		}
	}

	public void receive(Message msg) {
		if(peer.getMessageHistory().add(msg) && msg.getSender() != peer.getPeerId()) {
			if(msg instanceof MsgMessage) {
				record((MsgMessage)msg);
				broadcast(msg);
			} else if(msg instanceof CtrlMessage) {
				enact((CtrlMessage)msg);
			}
		}
	}

	public void record(MsgMessage msg) {
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

	public void enact(CtrlMessage msg) {
		peer.join(msg.getConnection());
	}

	public void broadcast(Message msg) {
		ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
		int numPeers = peerHandlers.size();

		int i = 0;
		while(i < numPeers) {
			try {
				peerHandlers.get(i).talk(msg);
			} catch(NullPointerException npe) {
				// i^th Peer left, so:
				//  - Remove i^th PeerHandler
				//  - Try again with (i+1)^th Peer now in i^th position
				peerHandlers.get(i).stop();
				continue;
			}

			i++;
		}
	}

	public void broadcastExit() {
		ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
		int numPeers = peerHandlers.size();
		InetAddress nextPeer;

		int i = 1;
		while(i < numPeers) {
			try {
				nextPeer = peerHandlers.get(i).getRemoteAddress();
			} catch(NullPointerException npe) {
				// i^th Peer left, so:
				//  - Remove i^th PeerHandler
				//  - Try again with (i+1)^th Peer now in i^th position
				peerHandlers.get(i).stop();
				continue;
			}

			try {
				peerHandlers.get(i - 1).talk(new CtrlMessage(nextPeer, peer.getPeerId()));
			} catch(NullPointerException npe) {
				// (i-1)^th Peer gone, so:
				//  - Remove (i-1)^th PeerHandler
				//  - Try again with i^th Peer now in (i-1)^th position
				peerHandlers.get(--i).stop();
				continue;
			}

			i++;
		}
	}
}
