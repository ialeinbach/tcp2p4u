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

  /**
   * Receives parameters into fields. Creates PeerListener and PeerSpeaker objects
   * and initializes them to track a Socket corresponding to a remote Peer.
   *
   * @param peer the parent Peer
   * @param socket a Socket to a remote Peer
   */
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

  /**
   * Returns the InetAddress of the remote Peer associated with this PeerHandler.
   *
   * @return the InetAddress of the remote Peer
   */
  public InetAddress getRemoteAddress() {
    return ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
  }

  /**
   * Starts the PeerListener listening to the remote Peer on a new Thread.
   */
  public void listen() {
    new Thread(getPeerListener()).start();
  }

  /**
   * Uses the PeerSpeaker to send a message to the remote Peer.
   *
   * @param msg an outgoing Message object
   */
  public void talk(Message msg) {
    peerSpeaker.writeMessage(msg);
  }

  /**
   * Returns the status of the corresponding PeerListener.
   *
   * @return whether corresponding PeerListener is active
   */
  public boolean status() {
    return getPeerListener().status();
  }

  /**
   * Returns the corresponding PeerListener.
   *
   * @return corresponding PeerListener
   */
  public PeerListener getPeerListener() {
    return peerListener;
  }

  /**
   * Returns the corresponding PeerSpeaker.
   *
   * @return corresponding PeerSpeaker
   */
  public PeerSpeaker getPeerSpeaker() {
    return peerSpeaker;
  }

  /**
   * Returns the Socket to the remote Peer.
   *
   * @return a Socket to the remote Peer
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * Stops this PeerHandler safely. Ensures PeerListener has stopped, then closes the Socket
   * to the remote Peer, consequently closing the corresponding I/O streams. Finally, the
   * PeerHandler removes itself from the parent Peer's collection of PeerHandlers.
   */
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
