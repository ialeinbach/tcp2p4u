import java.io.Serializable;
import java.util.Date;

public abstract class Message implements Serializable {
	private long timestamp;
	private int sender;

	public Message(int sender) {
		this.sender = sender;
		this.timestamp = new Date().getTime();
	}

	public int getSender() {
		return this.sender;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	// https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method
	public int hashCode() {
		long t = this.getTimestamp();
		int s = this.getSender();

		int result = 17;

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

		return this.getTimestamp() == other.getTimestamp() &&
			   this.getSender() == other.getSender();
	}
}
