# TCP2P4U

TCP2P4U is an in-terminal peer-to-peer TCP chat.

## Usage

Use ```javac @src.txt``` to compile.

For first peer in network, run ```java Peer 0 &```.   
The IP address of an existing peer in the network, must be hardcoded into the main method 
of ```Peer.java``` to add subsequent peers.   
Run ```java Peer X &``` to create a peer with peerId = X.    

Run ```java PeerRequest``` with command line arguments to make requests to your locally 
hosted Peer.   
Currently, requests are just printed by the receiving Peer with no behavior, so there is no 
way to send messages in the current build.
