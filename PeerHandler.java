import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class PeerHandler extends Observable implements Observer {
	private PeerListener peerListener;
	private PeerSpeaker peerSpeaker;
	private Socket socket;
	private int peerId;

	public PeerHandler(Peer peer, Socket socket) {
		try {
			this.peerId = peer.getPeerId();
			this.socket = socket;
			this.peerSpeaker = new PeerSpeaker(new ObjectOutputStream(socket.getOutputStream()), this.peerId);
			this.peerListener = new PeerListener(peer, new ObjectInputStream(socket.getInputStream()), this.peerId);
			this.peerListener.addObserver(peer.getEchoHandler());
			this.listen();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void update(Observable obs, Object obj) {
		if(obs instanceof PeerListener) {
			this.talk((Message)obj);
		}
	}

	public void listen() {
		new Thread(this.peerListener).start();
	}

	public void talk(Message msg) {
		this.peerSpeaker.writeMessage(msg);
	}
}
