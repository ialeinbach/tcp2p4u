import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class PeerHandler {
  private final PeerSpeaker peerSpeaker;
  private final PeerListener peerListener;
  private final Socket socket;
  private final Peer peer;

  public PeerHandler(Peer peer, Socket socket) {
    this.peer = peer;
    this.socket = socket;

    PeerSpeaker ps = null;
    PeerListener pl = null;

    try {
      ps = new PeerSpeaker(this, new ObjectOutputStream(socket.getOutputStream()));
      pl = new PeerListener(peer, this, new ObjectInputStream(socket.getInputStream()));
    } catch (EOFException eofe) {
      System.out.println("Remote peer left unexpectedly.");
      stop();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    peerListener = pl;
    peerSpeaker = ps;

    try {
      peerListener.addObserver(peer.getEchoHandler());
      listen();
    } catch (NullPointerException npe) {
      System.out.println("Remote peer left unexpectedly.");
      stop();
    }
  }

  public InetAddress getRemoteAddress() {
    return ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
  }

  public void listen() {
    new Thread(getPeerListener()).start();
  }

  public void talk(Message msg) {
    peerSpeaker.writeMessage(msg);
  }

  public boolean status() {
    return getPeerListener().status();
  }

  public PeerListener getPeerListener() {
    return peerListener;
  }

  public PeerSpeaker getPeerSpeaker() {
    return peerSpeaker;
  }

  public Socket getSocket() {
    return socket;
  }

  public void stop() {
    try {
      PeerListener peerListener = getPeerListener();
      if (peerListener.status()) {
        peerListener.stop();
      } else {
        getSocket().close();
        peer.getPeerHandlers().remove(this);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
