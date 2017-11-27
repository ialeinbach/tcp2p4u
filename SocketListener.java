import java.util.concurrent.SynchronousQueue;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Observable;

public class SocketListener extends Observable implements Runnable {
	private ServerSocket serverSocket;
	private Peer peer;
	private ArrayList<PeerHandler> peerHandlers;
	private Boolean active;
	private int peerCount;

	public SocketListener(Peer peer, int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.peerHandlers = peer.getPeerHandlers();
		this.peer = peer;
		this.active = false;
	}

	public int getNextPeerId() {
		return this.peer.getNextPeerId();
	}

	public void run() {
		this.active = true;

		while(this.active) {
			try {
				Socket skt = this.serverSocket.accept();		// blocking
				this.peerHandlers.add(new PeerHandler(this.peer, skt));
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}
}
