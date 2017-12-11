import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PeerListener extends Observable implements Runnable {
  private final ObjectInputStream input;
  private boolean active;
  private PeerHandler peerHandler;

  // given EchoHandler, PeerHandler as Observers in PeerHandler constructor
  public PeerListener(Peer peer, PeerHandler peerHandler, ObjectInputStream input) {
    this.input = input;
    this.peerHandler = peerHandler;
    active = false;
  }

  public boolean status() {
    return active;
  }

  public void stop() {
    active = false;
    peerHandler.stop();
  }

  public void run() {
    active = true;

    while (active) {
      try {
        Object incoming = input.readObject();
        if (incoming instanceof Message) {
          setChanged();
          notifyObservers((Message)incoming);
        }
      } catch (SocketException sce) {
        // Local Peer stopping...
      } catch (EOFException eofe) {
        // Remote Peer left...
        stop();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
