import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.Charset;

public class PeerRequester {
  private static final int port = 8888;

  /**
   * Checks whether command line arguments comprise a valid instruction and
   * handles request if it is the case.
   *
   * @param instruction instructions from the user
   */
  public PeerRequester(String[] instruction) {
    if (isValidInstruction(instruction)) {
      request(instruction);
    } else {
      System.out.println("Invalid request.");
    }
  }

  /**
   * Handles some instructions locally or writes it as a Request to
   * the locally hosted Peed.
   *
   * @param instruction instructions from the user
   */
  public void request(String[] instruction) {
    if (instruction.length >= 2 && instruction[0].equals("start")) {
      new Thread(new Peer(Integer.parseInt(instruction[1]))).start();
      return;
    }

    try {
      ObjectOutputStream output = new ObjectOutputStream(osToLocalPeer());
      output.writeObject(new Request(instruction));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates Socket to locally hosted Peer and returns its OutputStream.
   *
   * @return an OutputStream to the locally hosted Peer
   */
  public OutputStream osToLocalPeer() {
    OutputStream os = null;

    try {
      Socket skt = new Socket((String)null, port);
      os = skt.getOutputStream();
    } catch (ConnectException ce) {
      System.out.println("Couldn't connect to Peer.");
      System.exit(1);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    return os;
  }

  /**
   * Checks whether some instructions from the user are valid.
   *
   * @param instruction instructions from the user
   *
   * @return whether instruction is valid
   */
  public boolean isValidInstruction(String[] instruction) {
    for (String arg : instruction) {
      if (arg.contains(";")) {
        return false;
      }
    }

    return true;
  }

  /**
   *  Passes command line arguments to constructor, where class behavior lies.
   */
  public static void main(String[] args) {
    new PeerRequester(args);
  }
}
