import java.net.InetAddress;

public class CtrlMessage extends Message {
  private InetAddress connection;

  public CtrlMessage(InetAddress connection, int sender) {
    super(sender);
    this.connection = connection;
  }

  public InetAddress getConnection() {
    return connection;
  }

  public String toString() {
    return "[Peer " + getSender() + " disconnecting] Connect to: " + getConnection();
  }
}
