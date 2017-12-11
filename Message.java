import java.io.Serializable;
import java.util.Date;

public abstract class Message implements Serializable {
  private final long timestamp;
  private final int sender;

  /**
   * Receives parameters into fields. Records time that this Message was
   * created.
   *
   * @param sender the peerId of the original sender of the Message
   */
  public Message(int sender) {
    this.sender = sender;
    this.timestamp = new Date().getTime();
  }

  /**
   * Returns the peerId of the Peer who originally sent this Message.
   *
   * @return peerId of the original sender Peer
   */
  public int getSender() {
    return sender;
  }

  /**
   * Returns the timestamp of when this Message was created.
   *
   * @return timestamp of this Message
   */
  public long getTimestamp() {
    return timestamp;
  }

  // https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method
  /**
   * Implementation of hashCode() for this custom object.
   *
   * @return hash of Message
   */
  @Override
  public int hashCode() {
    long timestamp = getTimestamp();
    int sender = getSender();

    int result = 17;

    result = 37 * result + (int)(timestamp ^ (timestamp >>> 32)); // >>> is a logical right shift
    result = 37 * result + sender;

    return result;
  }

  /**
   * Implementation of equals() for this custom object.
   *
   * @param obj an Object to check equality with
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Message)) {
      return false;
    }

    Message other = (Message)obj;

    return getTimestamp() == other.getTimestamp()
           && getSender() == other.getSender();
  }
}
