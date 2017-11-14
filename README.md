# TCP2P4U

TCP2P4U is an in-terminal peer-to-peer TCP chat.

## Usage

Use ```javac @src.txt``` to compile.

For first peer in network run ```java PeerStarterTest```.
For subsequent peers, run ```java PeerTest X``` where X is the peerId for that peer.

Note that PeerStarterTest makes a peer whose peerId is 0.

The IP address of an existing peer in the network must be hardcoded into  ```PeerTest.java```.
