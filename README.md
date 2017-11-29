# TCP2P4U

TCP2P4U is an in-terminal peer-to-peer TCP chat.

## Usage

Use ```javac @src.txt``` to compile.

For first peer in network run ```java Peer 0 &```.
For subsequent peers, run ```java PeerTest X &``` where X is the peerId for that peer.
The IP address of an existing peer in the network must be hardcoded into the main method of ```Peer.java```.

Run ```java PeerRequest``` with arguments to make requests to your Peer.
Currently, requests are just printed by the receiving Peer with no behavior, so there is no 
way to send messages in the current build.
