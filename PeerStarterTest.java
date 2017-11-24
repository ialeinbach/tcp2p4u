import java.net.InetAddress;

public class PeerStarterTest {
	public static void main(String[] args) {
		Peer p = new Peer(0, 40000);
		new Thread(p).start();
	}
}
