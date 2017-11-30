import java.net.Socket;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

public class PeerRequester {
	private ObjectOutputStream output;
	private static final int port = 8888;
	private Request request;

	public PeerRequester(String[] input) {
		for(String arg : input) {
			if(arg.contains(";")) {
				System.out.println("Illegal character \";\" ");
				System.exit(1);
			}
		}

		this.request = new Request(input);
	}

	public void connect() {
		try {
			Socket skt = new Socket((String)null, port);
			this.output = new ObjectOutputStream(skt.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void request() {
		try {
			this.output.writeObject(this.request);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		PeerRequester pr = new PeerRequester(args);
		pr.connect();
		pr.request();
		System.exit(0);
	}
}
