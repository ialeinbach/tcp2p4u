import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;

public class SocketListener extends Observable implements Runnable {
  private final ServerSocket serverSocket;
  private final Peer peer;
  private boolean active;

  /**
   * Receives parameters into fields. Creates ServerSocket to listen for
   * new remote Peers.
   *
   * @param peer the parent Peer
   * @param port the port on which to listen for new remote Peers
   */
  public SocketListener(Peer peer, int port) {
    ServerSocket ss = null;

    try {
      ss = new ServerSocket(port);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    serverSocket = ss;
    this.peer = peer;
    active = false;
  }

  /**
   * Returns the status of this SocketListener.
   *
   * @return whether this SocketListener is active
   */
  public boolean status() {
    return active;
  }

  /**
   * Stops the internal ServerSocket.
   */
  public void stop() {
    active = false;

    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

  }

  /**
   * Listens for new remote Peers. When one is found, creates wrapper PeerHandler
   * and adds to parent Peer's PeerHandler collection.
   */
  public void run() {
    active = true;

    while (active) {
      try {
        Socket sktToPeer = serverSocket.accept();
        ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
        synchronized(peerHandlers) {
          peerHandlers.add(new PeerHandler(peer, sktToPeer));
        }
      } catch (SocketException se) {
        // Local Peer stopping...
        stop();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }

    }
  }
}
