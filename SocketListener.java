import java.util.concurrent.SynchronousQueue;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;

public class SocketListener extends Thread {
	private ServerSocket serverSocket;
	private ArrayList<PeerHandler> peerHandlers;
	private SynchronousQueue<Message> msgQueue;
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

		this.msgQueue = peer.getMsgQueue();
		this.peerHandlers = peer.getPeerHandlers();
		this.peerCount = 0;
		this.active = false;
	}

	public int getNextPeerId() {
		return peerCount++;
	}

	public void run() {
		this.active = true;
		System.out.println("SocketListener started.");

		while(this.active) {
			try {
				Socket skt = this.serverSocket.accept();		// blocking
				System.out.println("[SocketListener] Found socket.");

				PeerHandler ph = new PeerHandler(skt, this.msgQueue, this.getNextPeerId());
				System.out.println("[SocketListener] Wrapped socket in peer handler.");

				ph.listen();
				this.peerHandlers.add(ph);
				System.out.println("[SocketListener] Filed peer handler.");

			} catch(IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}

		}
	}

}
