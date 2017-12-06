import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class SocketListener extends Observable implements Runnable {
	private final ServerSocket serverSocket;
	private final Peer peer;
	private boolean active;

	public SocketListener(Peer peer, int port) {
		ServerSocket ss = null;

		try {
			ss = new ServerSocket(port);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		serverSocket = ss;
		this.peer = peer;
		active = false;
	}

	public boolean status() {
		return active;
	}

	public void stop() {
		active = false;

		if(!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

	}

	public void run() {
		active = true;

		while(active) {
			try {
				Socket sktToPeer = serverSocket.accept();		// blocking
				peer.getPeerHandlers().add(new PeerHandler(peer, sktToPeer));
			} catch(SocketException se) {
				// thrown when serverSocket.close() is called
				stop();
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}
}
