public class MsgMessage extends Message {
  private String content;

  /**
   * Receives parameters as fields.
   *
   * @param content body of Message
   *
   * @see Message.java
   */
  public MsgMessage(String content, int sender) {
    super(sender);
    this.content = content;
  }

  /**
   * Return content of this Message
   *
   * @return content of this Message
   */
  public String getContent() {
    return content;
  }

  /**
   * Returns String representation of this MsgMessage
   *
   * @return String representation of this MsgMessage
   */
  @Override
  public String toString() {
    return "[From Peer " + getSender() + "] " + getContent();
  }
}
