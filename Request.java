import java.io.Serializable;

public class Request implements Serializable {
	private final String command;
	private final String[] arguments;

	public Request(String[] raw) {
		int reqSize = raw.length;

		if(reqSize < 2) {
			this.command = null;
			this.arguments = null;
			return;
		}

		this.command = raw[1];

		if(reqSize == 2) {
			this.arguments = null;
			return;
		}

		String[] arguments = new String[reqSize - 2];

		for(int i = 2; i < reqSize; i++) {
			arguments[i - 2] = raw[i];
		}

		this.arguments = arguments;
	}

	public Request(String raw) {
		this(raw.split(";"));
	}

	public String getCommand() {
		return this.command;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	public String toString() {
		String command = this.getCommand();
		String[] arguments = this.getArguments();

		String out = "[REQUEST] ";
		out += command != null ? command : "NO_CMD";

		if(arguments != null && arguments.length > 0) {
			for(String arg : arguments) {
				out += " <" + arg + ">";
			}
		} else {
			out += "NO_ARGS";
		}

		return out;
	}
}
