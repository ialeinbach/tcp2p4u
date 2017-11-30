import java.net.Socket;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class PeerRequester {
	private OutputStream output;
	private static final int port = 8888;
	private String input;

	public PeerRequester(String[] input) {
		for(String arg : input) {
			if(arg.contains(";")) {
				System.out.println("Illegal character \";\" ");
				System.exit(1);
			}
		}

		this.input = String.join(";", input);
	}

	public void connect() {
		try {
			Socket skt = new Socket((String)null, port);
			this.output = skt.getOutputStream();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void request() {
		byte[] inp = this.input.getBytes(Charset.forName("UTF-8"));

		System.out.println(new String(inp));

		try {
			this.output.write(inp);
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
