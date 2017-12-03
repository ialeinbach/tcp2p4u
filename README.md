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

`broadcast` takes one argument and sends it as a message to every Peer it's connected to.   
`stop` takes no arguments and terminates the locally running Peer. In its current state, this request may leave the network fractured.   
