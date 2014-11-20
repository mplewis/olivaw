from prototypes import Bot

import random
from collections import deque


class ZeroAccessBot(Bot):
    def __repr__(self):
        return ('<ZeroAccessBot: {}, v{}, {} known peers>'
                .format(self.address, self.version, len(self.peers)))

    def __init__(self, version=0):
        super().__init__()
        self.peers = deque()
        self.version = version

    def tick(self):
        # Grab a peer from the known peerlist
        peer = random.choice(self.peers)

        # If the peer knows more than 16 peers, receive 16 of them
        # Otherwise, receive all (1-15) peers
        if len(peer.peers) > 16:
            peer_bots = random.sample(list(peer.peers), 16)
        else:
            peer_bots = peer.peers

        unknown_peers = list(set(self.peers) - set(peer_bots))

        # Make space for new peers, if necessary; we can only watch 256 peers
        if len(self.peers) + len(unknown_peers) > 256:
            peers_to_remove = len(unknown_peers)
            while (len(self.peers) > 0 and peers_to_remove > 0):
                self.peers.popleft()
                peers_to_remove -= 1
        self.peers.extend(unknown_peers)

        if peer.version > self.version:
            self.version = peer.version
