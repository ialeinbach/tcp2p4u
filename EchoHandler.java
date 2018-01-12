import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

public class EchoHandler extends Observable implements Observer {
  private final Peer peer;

  /**
   * Receives parameters into fields.
   *
   * @param peer the Peer that owns this EchoHandler
   */
  public EchoHandler(Peer peer) {
    this.peer = peer;
  }


  /**
   * Verifies observed object is a PeerListener and that the object passed is
   * a Message object.
   *
   * @param obs the object that called notifyObservers()
   * @param obj the object passed by when notifyObservers() was called
   */
  public void update(Observable obs, Object obj) {
    if (obs instanceof PeerListener && obj instanceof Message) {
      receive((Message)obj);
    }
  }

  /**
   * Receives incoming Message object, stores in MessageHistory, and performs
   * actions that depend on the child class of Message the object is. If 
   * Message was already received, ignore it.
   *
   * <p>MsgMessage: record in chat and broadcast</p>
   * <p>CtrlMessage: connect Peer to specified address</p>
   *
   * @param msg an incoming Message object
   */
  public void receive(Message msg) {
    if (peer.getMessageHistory().add(msg)) {
      if (msg instanceof MsgMessage) {
        record((MsgMessage)msg);
        broadcast(msg);
      } else if (msg instanceof CtrlMessage) {
        peer.join(((CtrlMessage)msg).getConnection());
      }
    }
  }

  /**
   * Records a MsgMessage in the chat file.
   *
   * @param msg an incoming MsgMessage object
   */
  public void record(MsgMessage msg) {
    byte[] msgBytes = (msg.toString() + "\n").getBytes(Charset.forName("UTF-8"));

    try {
      Files.createFile(peer.getChatFilepath());
    } catch (FileAlreadyExistsException faee) {
      // File exists... Not creating...
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    try {
      Files.write(peer.getChatFilepath(), msgBytes, StandardOpenOption.APPEND);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Broadcasts a Message to all PeerHandlers.
   *
   * @param msg an outgoing Message object
   */
  public void broadcast(Message msg) {
    ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
    synchronized(peerHandlers) {
      for (PeerHandler ph : peerHandlers) {
        ph.talk(msg);
      }
    }
  }

  /**
   * Broadcast a CtrlMessage to PeerHandlers before stopping the Peer in
   * order to avoid network fracturing.
   */
  public void broadcastExit() {
    ArrayList<PeerHandler> peerHandlers = peer.getPeerHandlers();
    synchronized(peerHandlers) {
      int numPeers = peerHandlers.size();

      if (numPeers < 2) {
        return;
      }

      InetAddress prevPeer = peerHandlers.get(0).getRemoteAddress();
      PeerHandler currPeer;

      for(int i = 1; i < numPeers; i++) {
        currPeer = peerHandlers.get(i);
        currPeer.talk(new CtrlMessage(prevPeer, peer.getPeerId()));
        prevPeer = currPeer.getRemoteAddress();
      }
    }
  }
}
