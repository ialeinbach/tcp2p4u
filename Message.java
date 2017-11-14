import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	private String msg;
	private Date timestamp;
	private int sender;

	public Message(String msg, int sender) {
		this.msg = msg;
		this.sender = sender;
		this.timestamp = new Date();
	}

	public String getContent() {
		return this.msg;
	}

	public int getSender() {
		return this.sender;
	}

	public long getTimestamp() {
		return this.timestamp.getTime();
	}

	public String toString() {
		return "[" + this.sender + "] " + this.getContent();
	}

	// https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method
	public int hashCode() {
		String m = this.getContent();
		long t = this.getTimestamp();
		int s = this.getSender();

		int result = 17;
		int mHash = (m != null) ? m.hashCode() : 0;

		result = 37 * result + mHash;
		result = 37 * result + (int)(t ^ (t >>> 32));	// >>> is a logical right shift
		result = 37 * result + s;

		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if(!(obj instanceof Message)) {
			return false;
		}

		Message other = (Message)obj;

		return this.getContent().equals(other.getContent()) &&
			   this.sender == other.sender;
	}
}
