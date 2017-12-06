import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

public class PeerHandler extends Observable {
	private final PeerListener peerListener;
	private final PeerSpeaker peerSpeaker;
	private final Socket socket;
	private final Peer peer;

	public PeerHandler(Peer peer, Socket socket) {
		this.peer = peer;
		this.socket = socket;

		PeerListener pl = null;
		PeerSpeaker ps = null;

		try {
			pl = new PeerListener(peer, new ObjectInputStream(socket.getInputStream()));
			ps = new PeerSpeaker(new ObjectOutputStream(socket.getOutputStream()));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		peerListener = pl;
		peerSpeaker = ps;
		peerListener.addObserver(peer.getEchoHandler());
		listen();
	}

	public InetAddress getRemoteAddress() {
		return ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
	}

	public void listen() {
		new Thread(peerListener).start();
	}

	public void talk(Message msg) {
		peerSpeaker.writeMessage(msg);
	}

	public PeerListener getPeerListener() {
		return peerListener;
	}

	public PeerSpeaker getPeerSpeaker() {
		return peerSpeaker;
	}

	public Socket getSocket() {
		return socket;
	}

	public void stop() {
		peerListener.stop();

		try {
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
