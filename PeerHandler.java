import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

public class PeerHandler extends Observable implements Observer {
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
		peerListener.addObserver(this);
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

	public void update(Observable obs, Object obj) {
		if(obs instanceof PeerListener && obj == null) {
			stop(false);
		}
	}

	public void stop(boolean recursive) {
		if(recursive) {
			peerListener.stop();
		}

		try {
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		peer.getPeerHandlers().remove(this);
	}
}
