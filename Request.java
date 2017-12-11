import java.io.Serializable;

public class Request implements Serializable {
  private final String command;
  private final String[] arguments;

  public Request(String[] raw) {
    int reqSize = raw.length;

    if (reqSize < 1) {
      command = null;
      arguments = null;
    } else if (reqSize == 1) {
      command = raw[0];
      arguments = null;
    } else {
      String[] arguments = new String[reqSize - 1];

      for (int i = 1; i < reqSize; i++) {
        arguments[i - 1] = raw[i];
      }

      command = raw[0];
      this.arguments = arguments;
    }
  }

  public Request(String raw) {
    this(raw.split(";"));
  }

  public String getCommand() {
    return command;
  }

  public String[] getArguments() {
    return arguments;
  }

  public String toString() {
    String command = getCommand();
    String[] arguments = getArguments();

    String out = "[REQUEST] ";
    out += command != null ? command : "NO_CMD";

    if (arguments != null) {
      for (String arg : arguments) {
        out += " <" + arg + ">";
      }
    } else {
      out += " NO_ARGS";
    }

    return out;
  }
}
