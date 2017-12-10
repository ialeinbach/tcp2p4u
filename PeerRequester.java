import java.net.Socket;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class PeerRequester {
	private static final int port = 8888;

	public PeerRequester(String[] instruction) {
		if(isValidInstruction(instruction)) {
			request(instruction);
		} else {
			System.out.println("Invalid request.");
		}
	}

	public void request(String[] instruction) {
		if(instruction.length >= 2 && instruction[0].equals("start")) {
			new Thread(new Peer(Integer.parseInt(instruction[1]))).start();
			return;
		}

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

	public boolean isValidInstruction(String[] instruction) {
		for(String arg : instruction) {
			if(arg.contains(";")) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		new PeerRequester(args);
	}
}
