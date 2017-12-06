import java.net.Socket;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class PeerRequester {
	private static final int port = 8888;

	public PeerRequester(String[] instruction) {
		for(String arg : instruction) {
			if(arg.contains(";")) {
				System.out.println("Illegal character \";\" ");
				System.exit(1);
			}
		}

		request(instruction);
	}

	public void request(String[] instruction) {
		try {
			ObjectOutputStream output = new ObjectOutputStream(osToLocalPeer());
			output.writeObject(new Request(instruction));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public OutputStream osToLocalPeer() {
		OutputStream os = null;

		try {
			Socket skt = new Socket((String)null, port);
			os = skt.getOutputStream();
		} catch(ConnectException ce) {
			System.out.println("Couldn't connect to Peer.");
			System.exit(1);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return os;
	}

	public static void main(String[] args) {
		new PeerRequester(args);
	}
}
