import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PeerListener extends Observable implements Runnable {
  private final ObjectInputStream input;
  private final PeerHandler peerHandler;
  private boolean active;  

  /**
   * Receives parameters into fields.
   *
   * @param peerHandler the parent PeerHandler
   * @param input an ObjectInputStream through which a remote Peer sends Messages
   */
  public PeerListener(PeerHandler peerHandler, ObjectInputStream input) {
    this.input = input;
    this.peerHandler = peerHandler;
    active = false;
  }

  /**
   * Returns the status of this PeerListener.
   *
   * @return whether PeerListener is active
   */
  public boolean status() {
    return active;
  }

  /**
   * Stops parent PeerHandler.
   */
  public void stop() {
    peerHandler.stop();
  }

  /**
   * Receive Messages from the corrseponding remote Peer and passes them the parent PeerHandler.
   */
  public void run() {
    active = true;

    while (active) {
      try {
        Object incoming = input.readObject();
        if (incoming instanceof Message) {
          setChanged();
          notifyObservers((Message)incoming);
        }
      } catch (SocketException se) {
        // Local Peer stopping...
        active = false;
      } catch (EOFException eofe) {
        // Remote Peer left...
        active = false;
        stop();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
