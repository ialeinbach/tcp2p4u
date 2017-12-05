import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class PeerHandler extends Observable implements Observer {
	private PeerListener peerListener;
	private PeerSpeaker peerSpeaker;
	private Socket socket;

	public PeerHandler(Peer peer, Socket socket) {
		try {
			this.socket = socket;
			this.peerSpeaker = new PeerSpeaker(new ObjectOutputStream(socket.getOutputStream()));
			this.peerListener = new PeerListener(peer, new ObjectInputStream(socket.getInputStream()));
			this.peerListener.addObserver(peer.getEchoHandler());
			this.listen();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void update(Observable obs, Object obj) {
		this.talk((Message)obj);
	}

	public InetAddress getRemoteAddress() {
		return ((InetSocketAddress)this.socket.getRemoteSocketAddress()).getAddress();
	}

	public void listen() {
		new Thread(this.peerListener).start();
	}

	public void talk(Message msg) {
		this.peerSpeaker.writeMessage(msg);
	}

	public boolean checkListener() {
		return this.peerListener.status();
	}

	public void stop() {
		this.peerListener.stop();

		try {
			this.socket.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
