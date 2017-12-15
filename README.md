# TCP2P4U

TCP2P4U is an in-terminal peer-to-peer TCP chat.

## Usage

Run `make` to compile.   
Run `make starter` to compile and create a starter Peer.   
Run `make clean` to delete class files.   
Run `java Peer X &` to create a peer with a peerId of X.   
Run `java PeerRequester` with command line arguments to make requests to your locally hosted Peer.   

## Ports

Port 8888 is used to communicate with your locally hosted Peer.   
Port 40000 is used to communicate with remote Peers.   

## Requests

`broadcast` is variadic and sends arguments joined by semicolons as a message to every Peer to which the locally hosted Peer is connected.   

`stop` takes no arguments and terminates the locally hosted Peer. In its current state, this request may leave the network fractured.   

`info` prints out basic information about the locally running Peer.   

`chat` prints out the chat.   

`join` is variadic and instructs the locally hosted Peer to connect to remote Peers at given IPs.   

`start` takes no arguments and creates a locally hosted Peer.
