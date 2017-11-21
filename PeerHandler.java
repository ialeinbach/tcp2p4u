import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.SynchronousQueue;
import java.net.Socket;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class PeerHandler extends Observable implements Observer {
	private PeerListener peerListener;
	private PeerSpeaker peerSpeaker;
	private int peerId;

	public PeerHandler(Peer peer, Socket socket) {
		this.peerId = peer.getPeerId();

		ObjectOutputStream objOutput = null;
		ObjectInputStream objInput = null;

		try {
			objOutput = new ObjectOutputStream(socket.getOutputStream());
			objInput = new ObjectInputStream(socket.getInputStream());
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

		this.peerSpeaker = new PeerSpeaker(objOutput, this.peerId);
		this.peerListener = new PeerListener(peer, objInput, this.peerId);
		this.peerListener.addObserver(peer.getEchoHandler());
		this.listen();
	}

	public void update(Observable obs, Object obj) {
		if(obs instanceof PeerListener) {
			this.talk((Message)obj);
		}
	}

	public void listen() {
		new Thread(this.peerListener).start();
		System.out.println("[PeerHandler] Told peer listener to start listening.");
	}

	public void talk(Message msg) {
		this.peerSpeaker.writeMessage(msg);
		System.out.println("[PeerHandler] Told peer speaker to send a message.");
	}
}
