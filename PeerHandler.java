import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.SynchronousQueue;
import java.net.Socket;
import java.io.IOException;

public class PeerHandler {
	private SynchronousQueue<Message> msgQueue;
	private PeerListener peerListener;
	private PeerSpeaker peerSpeaker;
	private int peerId;

	public PeerHandler(Socket socket, SynchronousQueue<Message> msgQueue, int peerId) {
		this.msgQueue = msgQueue;
		this.peerId = peerId;

		ObjectOutputStream objOutput = null;
		ObjectInputStream objInput = null;

		try {
			objOutput = new ObjectOutputStream(socket.getOutputStream());
			objInput = new ObjectInputStream(socket.getInputStream());
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

		this.peerListener = new PeerListener(objInput, msgQueue, this.peerId);
		this.peerSpeaker = new PeerSpeaker(objOutput, this.peerId);
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
