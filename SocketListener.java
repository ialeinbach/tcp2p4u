import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class SocketListener extends Observable implements Runnable {
	private ServerSocket serverSocket;
	private Peer peer;
	private ArrayList<PeerHandler> peerHandlers;
	private boolean active;

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

	public boolean status() {
		return this.active;
	}

	public void stop() {
		this.active = false;

		try {
			this.serverSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		this.active = true;

		while(this.active) {
			try {
				Socket skt = this.serverSocket.accept();		// blocking
				this.peerHandlers.add(new PeerHandler(this.peer, skt));
			} catch(SocketException se) {
				// serverSocket.accept() throws SocketException when close() is called
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}
}
