import java.util.*;

public class GoodBot implements ZeroAccessBot {

    private static final int MAX_KNOWN_PEER_COUNT = 256;
    protected static final int PEERS_TO_RETURN = 16;
    protected static final Random rng = new Random();

    protected Deque<ZeroAccessBot> peers = new LinkedList<ZeroAccessBot>();

    private int version;

    public GoodBot() {
        version = 0;
    }

    @Override
    public int maxPeerCount() {
        return MAX_KNOWN_PEER_COUNT;
    }

    @Override
    public void adoptPeer(ZeroAccessBot newBot) {
        peers.add(newBot);
        while (peers.size() > MAX_KNOWN_PEER_COUNT) {
            this.peers.remove();
        }
    }

    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        // If you don't have enough peers, return all of them
        if (peers.size() <= PEERS_TO_RETURN) {
            return new ArrayList<ZeroAccessBot>(peers);
        }

        // Otherwise, return PEERS_TO_RETURN peers
        Set<ZeroAccessBot> selectedPeers = new HashSet<ZeroAccessBot>();
        while (selectedPeers.size() < PEERS_TO_RETURN) {
            int index = rng.nextInt(peers.size());
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
        int index = rng.nextInt(peers.size());
        ZeroAccessBot peer = peersList.get(index);

        // Add its known peers to own peer list, replacing existing peers
        List<ZeroAccessBot> receivedPeers = peer.knownPeers(this);
        for (ZeroAccessBot newPeer : receivedPeers) {
            peers.add(newPeer);
        }

        // Trim peers list to MAX_KNOWN_PEER_COUNT
        while (peers.size() > MAX_KNOWN_PEER_COUNT) {
            peers.remove();
        }

        // If the peer is more up-to-date, update self from peer
        if (peer.getVersion() > version) {
            version = peer.getVersion();
        }
    }

    public void setPeers(Deque<ZeroAccessBot> peers) {
        this.peers = peers;
    }

    public Deque<ZeroAccessBot> getPeers() {
        return peers;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

}
