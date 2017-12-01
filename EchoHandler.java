import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observer;
import java.util.Observable;

public class EchoHandler extends Observable implements Observer {
	private ArrayList<PeerHandler> peerHandlers;
	private HashSet<Message> msgHistory;
	private int peerId;

	public EchoHandler(Peer peer) {
		this.peerHandlers = peer.getPeerHandlers();
		this.msgHistory = peer.getMsgHistory();
		this.peerId = peer.getPeerId();
	}

	public void broadcast(Message msg) {
		int numPeers = this.peerHandlers.size();
		for(int i = 0; i < numPeers; i++) {
			this.peerHandlers.get(i).talk(msg);
		}
	}

	public void update(Observable obs, Object obj) {
		if(obj instanceof MsgMessage) {
			MsgMessage msg = (MsgMessage)obj;
			this.receive(msg);
		} else if(obj instanceof CtrlMessage) {
			CtrlMessage msg = (CtrlMessage)obj;
			this.receive(msg);
		}
	}

	public boolean receive(Message msg) {
		if(this.msgHistory.add(msg)) {
			if(msg.getSender() != this.peerId) {
				System.out.println(msg);
			}

			this.broadcast(msg);
			return true;
		}

		return false;
	}
}
