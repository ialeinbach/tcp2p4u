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
  private static final String chatFilename = ".peerchat.txt";

  private boolean active;
  private final int peerId;               // unique id for each Peer

  private final HashSet<Message> messageHistory;        // detect duplicate Messages received
  private final ArrayList<PeerHandler> peerHandlers;    // track adjacent Peers
  private final Path chatFilepath;

  private final SocketListener socketListener;      // listen for new Peers
  private final EchoHandler echoHandler;          // broadcast to all PeerHandlers
  private final ServerSocket requestListener;       // listen for Requests

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
    while (peerHandlers.size() > 0) {
      System.out.println("Stopping a PeerHandler.");
      peerHandlers.get(0).stop();
    }
  }

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

  public static boolean isRequestSocket(Socket requestSocket) {
    String local = requestSocket.getLocalSocketAddress().toString().split(":")[0];
    String remote = requestSocket.getRemoteSocketAddress().toString().split(":")[0];

    return local.equals(remote);
  }

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

  public String info() {
    String out = "--> peerId: " + getPeerId() + "\n"
           + "--> numPeers: " + getPeerHandlers().size() + "\n\n"
           + "Peer Addresses:\n";

    for (PeerHandler ph : getPeerHandlers()) {
      out += "  -->" + ph.getRemoteAddress().toString() + "\n";
    }

    return out;
  }

  public boolean status() {
    return active;
  }

  public int getPeerId() {
    return peerId;
  }

  public HashSet<Message> getMessageHistory() {
    return messageHistory;
  }

  public ArrayList<PeerHandler> getPeerHandlers() {
    return peerHandlers;
  }

  public Path getChatFilepath() {
    return chatFilepath;
  }

  public SocketListener getSocketListener() {
    return socketListener;
  }

  public EchoHandler getEchoHandler() {
    return echoHandler;
  }

  public ServerSocket getRequestListener() {
    return requestListener;
  }
}
