from prototypes import Bot

import random
from collections import deque


class ZeroAccessBot(Bot):
    """
    ZeroAccess bot. See the Kindsight paper for details.

    The bot keeps a list of 256 peers. Every second, it sends a UDP packet to
    one of these peers. The peer being queried responds with the following:

    * A list of 16 of its peers' addresses
    * The version (file name and creation date) of its payload

    The bot updates its peer list with other known peers. If its payload is out
    of date, it updates its payload. This is represented by the version number
    increasing to match the peer with the more recent payload.
    """
    def __repr__(self):
        return ('<ZeroAccessBot: {}, v{}, {} known peers>'
                .format(self.address, self.version, len(self.peers)))

    def __init__(self, address, version=0):
        super().__init__()
        self.address = address
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

        # Make space for new peers, if necessary; we can only watch 256 peers
        if len(self.peers) + 16 > 256:
            peers_to_remove = 16
            while (len(self.peers) > 0 and peers_to_remove > 0):
                self.peers.popleft()
                peers_to_remove -= 1
        self.peers.extend(peer_bots)

        if peer.version > self.version:
            self.version = peer.version
