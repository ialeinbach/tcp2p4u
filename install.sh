#!/bin/bash

mkdir ~/.local/bin

mkdir ~/.local/lib

curl -o ~/.local/lib/PeerRequester.jar URL

echo "#!/bin/bash" >> ~/.local/bin/peer
echo "java -jar ~/lib/PeerRequester.jar" >> ~/.local/bin/peer

chmod 755 ~/.local/bin/peer
