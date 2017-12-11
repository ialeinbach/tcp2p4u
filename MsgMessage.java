public class MsgMessage extends Message {
  private String content;

  public MsgMessage(String content, int sender) {
    super(sender);
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public String toString() {
    return "[From Peer " + getSender() + "] " + getContent();
  }
}
