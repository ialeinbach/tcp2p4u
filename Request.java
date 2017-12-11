import java.io.Serializable;

public class Request implements Serializable {
  private final String command;
  private final String[] arguments;

  /**
   * Receives parameters into fields. Parses command line input as
   * an instruction.
   *
   * @param raw instructions from the user
   */
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

  /**
   * Split a String by the control character and call main constructor.
   *
   * @param raw instructions from the user as a semicolon-separated string
   */
  public Request(String raw) {
    this(raw.split(";"));
  }

  /**
   * Returns the command of the Request.
   *
   * @return command of the Request
   */
  public String getCommand() {
    return command;
  }

  /**
   * Returns the arguments of the Request.
   *
   * @return arguments of the Request
   */
  public String[] getArguments() {
    return arguments;
  }

  /**
   * Returns String representation of this Request.
   *
   * @return String represenattion of this Request
   */
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
