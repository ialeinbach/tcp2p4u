import java.io.ObjectOutputStream;
import java.net.SocketException;

public class PeerSpeaker {
  private final ObjectOutputStream output;
  private final PeerHandler peerHandler;

  /**
   * Receives arguments into fields.
   *
   * @param peerHandler parent PeerHandler
   * @param output an ObjectOutputStream through which the parent Peer sends
   *               Messages to a remote Peer
   */

  public PeerSpeaker(PeerHandler peerHandler, ObjectOutputStream output) {
    this.peerHandler = peerHandler;
    this.output = output;
  }

  /**
   *  Stops parent PeerHandler.
   */
  public void stop() {
    peerHandler.stop();
  }

  /**
   * Writes a Message to the corresponding remote Peer.
   */
  public void writeMessage(Message msg) {
    try {
      output.writeObject(msg);
    } catch (NullPointerException npe) {
      System.out.println("Remote Peer left unexpectedly.");
      stop();
    } catch (SocketException se) {
      System.out.println("Remote Peer left unexpectedly.");
      stop();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
