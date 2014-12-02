package src;

import java.util.*;

public class GoodBot implements ZeroAccessBot {

    private static final int KNOWN_PEER_COUNT = 256;
    private static final int PEERS_TO_RETURN = 16;
    private static final Random rng = new Random();

    private final Deque<ZeroAccessBot> peers = new LinkedList<ZeroAccessBot>();

    private int version;

    public GoodBot() {
        version = 0;
    }

    @Override
    public List<ZeroAccessBot> knownPeers() {
        Set<ZeroAccessBot> selectedPeers = new HashSet<ZeroAccessBot>();
        while (selectedPeers.size() < PEERS_TO_RETURN) {
            int index = rng.nextInt(KNOWN_PEER_COUNT);
            LinkedList<ZeroAccessBot> peersList = (LinkedList<ZeroAccessBot>) peers;
            ZeroAccessBot peer = peersList.get(index);
            selectedPeers.add(peer);
        }
        return new ArrayList<ZeroAccessBot>(selectedPeers);
    }

    @Override
    public void tick() {
        // Grab a random known peer
        LinkedList<ZeroAccessBot> peersList = (LinkedList<ZeroAccessBot>) peers;
        int index = rng.nextInt(KNOWN_PEER_COUNT);
        ZeroAccessBot peer = peersList.get(index);

        // Add its known peers to own peerlist, replacing existing peers
        List<ZeroAccessBot> rcvdPeers = peer.knownPeers();
        for (ZeroAccessBot newPeer : rcvdPeers) {
            peers.remove();
            peers.add(newPeer);
        }

        // If the peer is more up-to-date, update self from peer
        if (peer.getVersion() > version) {
            version = peer.getVersion();
        }
    }

    public int getVersion() {
        return version;
    }
}
