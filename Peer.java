import java.io.File;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Peer implements Runnable {
  private static final int REQ_PORT = 8888;
  private static final int COM_PORT = 40000;
  private static final String chatFilename = ".peerchat";

  private boolean active;
  private final int peerId;               // unique id for each Peer

  private final HashSet<Message> messageHistory;        // detect duplicate Messages received
  private final ArrayList<PeerHandler> peerHandlers;    // track adjacent Peers
  private final Path chatFilepath;

  private final SocketListener socketListener;      // listen for new Peers
  private final EchoHandler echoHandler;          // broadcast to all PeerHandlers
  private final ServerSocket requestListener;       // listen for Requests

  /**
   * Receives parameters into fields. Determines chat filepath, delete existing chat file, and
   * create a new, blank one. Start a ServerSocket listening for PeerRequesters.
   *
   * @param peerId unique id of this Peer
   */
  public Peer(int peerId) {
    active = false;
    this.peerId = peerId;

    messageHistory = new HashSet<Message>();
    peerHandlers = new ArrayList<PeerHandler>();
    chatFilepath = Paths.get(System.getProperty("user.home") + "/" + Peer.chatFilename);
    resetChatHistory();

    ServerSocket ss = null;

    try {
      ss = new ServerSocket(REQ_PORT);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    requestListener = ss;
    echoHandler = new EchoHandler(this);
    socketListener = new SocketListener(this, COM_PORT);
  }

  /**
   * Listens for PeerRequesters to receive Requests and handle them.
   */
  public void run() {
    active = true;
    new Thread(getSocketListener()).start();

    while (active) {
      try {
        Socket request = getRequestListener().accept();
        handleRequest(request);
        request.close();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  /**
   * Connect to a remote Peer.
   *
   * @param addrRemotePeer an InetAddress to a remote Peer
   */
  public void join(InetAddress addrRemotePeer) {
    try {
      Socket sktRemotePeer = new Socket(addrRemotePeer, COM_PORT);
      getPeerHandlers().add(new PeerHandler(this, sktRemotePeer));
    } catch (ConnectException ce) {
      System.out.println("No peer found at address " + addrRemotePeer.toString());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Stop the Peer, killing all threads and deleting the chat file.
   */
  public void stop() {
    active = false;

    try {
      Files.deleteIfExists(getChatFilepath());
      getRequestListener().close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    getSocketListener().stop();
    getEchoHandler().broadcastExit();

    ArrayList<PeerHandler> peerHandlers = getPeerHandlers();
    synchronized(peerHandlers) {
      while (peerHandlers.size() > 0) {
        System.out.println("Stopping a PeerHandler.");
        PeerHandler ph = peerHandlers.get(0);
        synchronized(ph) {
          ph.stop();
        }
        peerHandlers.remove(ph);
      }
    }
  }

  /**
   * Safely remove a PeerHandler from the ArrayList of PeerHandlers.
   *
   * @param ph PeerHandler to be removed.
   */
  public void forgetPeerHandler(PeerHandler ph) {
    ArrayList<PeerHandler> peerHandlers = getPeerHandlers();
    synchronized(peerHandlers) {
      peerHandlers.remove(ph);
    }
  }

  /**
   * Handles Requests from a PeerRequester.
   *
   * @see README.md
   * <p>STOP, BROADCAST, INFO, CHAT, JOIN</p>
   *
   * @param socket a Socket to a PeerRequester
   */
  public void handleRequest(Socket socket) {
    if (!isRequestSocket(socket)) {
      return;
    }

    Request request = null;

    try {
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      request = (Request)in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println(request);
    String command = request.getCommand();

    if (command.equals("stop")) {
      stop();
    } else if (command.equals("broadcast")) {
      MsgMessage out = new MsgMessage(String.join("; ", request.getArguments()), getPeerId());
      getEchoHandler().receive(out);
    } else if (command.equals("info")) {
      System.out.println(info());
    } else if (command.equals("chat")) {
      System.out.println();
      Scanner scanner = null;

      try {
        scanner = new Scanner(new File(getChatFilepath().toString()));
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
      }
    } else if (command.equals("join")) {
      for (String arg : request.getArguments()) {
        if (isValidAddress(arg)) {
          try {
            join(InetAddress.getByName(arg));
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
          }
        } else {
          System.out.println("\"" + arg + "\" is not a valid IP address.");
        }
      }
    }
  }

  /**
   * Checks whether a String is a valid IP address.
   *
   * @param rawAddress a String that may contain an IP address
   *
   * @return whether rawAddress is a valid IP address
   */
  public static boolean isValidAddress(String rawAddress) {
    String[] address = rawAddress.split("\\.");

    if (address.length != 4) {
      return false;
    }

    for (String octet : address) {
      if (!octet.matches("\\d+")) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks whether a Socket is bound locally on both ends.
   *
   * @param requestSocket a Socket object
   *
   * @return whether requestSocket is likely from a PeerRequester
   */
  public static boolean isRequestSocket(Socket requestSocket) {
    String local = requestSocket.getLocalSocketAddress().toString().split(":")[0];
    String remote = requestSocket.getRemoteSocketAddress().toString().split(":")[0];

    return local.equals(remote);
  }

  /**
   * Delete any existing chat files and create a new one.
   */
  public void resetChatHistory() {
    Path chatFilepath = getChatFilepath();

    try {
      Files.deleteIfExists(chatFilepath);
      Files.createFile(chatFilepath);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Print info about Peer.
   */
  public String info() {
    String out;

    ArrayList<PeerHandler> peerHandlers = getPeerHandlers();
    synchronized(peerHandlers) {
      out = "--> peerId: " + getPeerId() + "\n"
          + "--> numPeers: " + getPeerHandlers().size() + "\n\n"
          + "Peer Addresses:\n";

      for (PeerHandler ph : getPeerHandlers()) {
        out += "  -->" + ph.getRemoteAddress().toString() + "\n";
      }
    }
    
    return out;
  }

  /**
   * Returns the status of this Peer.
   *
   * @return whether this Peer is active
   */
  public boolean status() {
    return active;
  }

  /**
   * Returns the peerId of this Peer.
   *
   * @return the peerId of this Peer
   */
  public int getPeerId() {
    return peerId;
  }

  /**
   * Returns the Message history of this Peer.
   *
   * @return the Message history of this Peer
   */
  public HashSet<Message> getMessageHistory() {
    return messageHistory;
  }

  /**
   * Returns the ArrayList of PeerHandlers that each wrap a remote Peer.
   *
   * @return ArrayList of PeerHandlers that each wrap a remote Peers
   */
  public ArrayList<PeerHandler> getPeerHandlers() {
    return peerHandlers;
  }

  /**
   * Returns the path to the chat file for this Peer.
   *
   * @return the path to the chat file
   */
  public Path getChatFilepath() {
    return chatFilepath;
  }

  /**
   * Returns the SocketListener of this Peer.
   *
   * @return the SocketListener of this Peer
   */
  public SocketListener getSocketListener() {
    return socketListener;
  }

  /**
   * Returns the EchoHandler of this Peer.
   *
   * @return the EchoHandler of this Peer
   */
  public EchoHandler getEchoHandler() {
    return echoHandler;
  }

  /**
   * Returns the RequestListener of this Peer.
   *
   * @return the RequestListener of this Peer
   */
  public ServerSocket getRequestListener() {
    return requestListener;
  }
}
