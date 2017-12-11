import java.net.InetAddress;

public class CtrlMessage extends Message {
  private InetAddress connection;

  /**
   * Receives parameters into fields.
   *
   * @param connection InetAddress to a remote Peer
   *
   * @see Message.java
   */
  public CtrlMessage(InetAddress connection, int sender) {
    super(sender);
    this.connection = connection;
  }

  /**
   * Return connection associated with this CtrlMessage
   *
   * @return connection associated with this CtrlMessage
   */
  public InetAddress getConnection() {
    return connection;
  }

  /**
   * Returns String representation of this CtrlMessage
   *
   * @return String representation of this CtrlMessage
   */
  public String toString() {
    return "[Peer " + getSender() + " disconnecting] Connect to: " + getConnection();
  }
}
