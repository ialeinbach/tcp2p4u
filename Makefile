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
	@javac $(ALL)
	@echo "DONE"

starter:
	@echo -n "Compiling..."
	@javac $(ALL)
	@echo "DONE"
	@echo -n "Creating starter peer..."
	@java PeerRequester start 0 &
	@echo "DONE"

clean:
	@echo -n "Cleaning up..."
	@rm *.class
	@echo "DONE"
