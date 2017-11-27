import java.net.InetAddress;
import java.net.UnknownHostException;

public class PeerTest {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("[Peer] Invalid arguments");
			System.exit(1);
		}

		int peerId = Integer.parseInt(args[0]);
		Peer p = new Peer(peerId, 8080);

		byte[] addr = {(byte)143, (byte)229, (byte)6, (byte)111};

		try {
			InetAddress host = InetAddress.getByAddress(addr);
			p.join(host, 8080);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		new Thread(p).start();
	}
}
