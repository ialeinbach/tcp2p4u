JC = javac
ALL = Message.java \
CtrlMessage.java \
MsgMessage.java \
PeerSpeaker.java \
PeerListener.java \
SocketListener.java \
EchoHandler.java \
PeerHandler.java \
Peer.java \
PeerRequester.java \
Request.java

default:
	@echo -n "Compiling..."
	@$(JC) $(ALL)
	@echo "DONE"

clean:
	@echo -n "Cleaning up..."
	@rm *.class
	@echo "DONE"
