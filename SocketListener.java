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
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

		System.out.println("[SocketListener] Created internal ServerSocket object.");

		this.peerHandlers = peer.getPeerHandlers();
		this.active = false;
	}

	public int getNextPeerId() {
		return this.peer.getNextPeerId();
	}

	public void run() {
		this.active = true;
		System.out.println("SocketListener started.");

		while(this.active) {
			try {
				Socket skt = this.serverSocket.accept();		// blocking
				System.out.println("[SocketListener] Found socket.");

				this.peerHandlers.add(new PeerHandler(this.peer, skt));
				System.out.println("[SocketListener] Filed peer handler.");
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}
}
