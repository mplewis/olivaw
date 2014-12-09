import java.util.*;

public class GoodBot extends ZeroAccessBot {

    private static final int MAX_KNOWN_PEER_COUNT = 256;
    protected static final int PEERS_TO_RETURN = 16;
    protected static final Random rng = new Random();

    protected Deque<PeerBlock> peers = new LinkedList<PeerBlock>();

    private int version;
    private boolean permanentlyDown = false;
    private int downTimeLeft = 0;

    public GoodBot() {
        super();
        version = 0;
    }

    protected boolean isDown() {
        return permanentlyDown || downTimeLeft > 0;
    }

    private boolean maybeGoDown() {
        int num = Math.abs(rng.nextInt());
        if ( num % 3380 < 1 ) {
            // 1/10 bots go down for 6h/day
            downTimeLeft = 84;
            return true;
        }
        if ( num % 60840 < 1 ) {
            // go down for a day every 6 months
            downTimeLeft = 338*1;
            return true;
        }
        if ( num % 10000000 < 1 ) {
            // one in a million chance of dying
            permanentlyDown = true;
            return true;
        }
        return false;
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
    public PeerBlock knownPeers(ZeroAccessBot caller) {
        if (isDown() || peers.isEmpty()) {
            return null;
        }
        int index = rng.nextInt(peers.size());
        return ( (LinkedList<PeerBlock>) peers).get(index);
    }

    @Override
    public void tick() {
        if ( isDown() ) {
            downTimeLeft--;
            return;
        } else if ( maybeGoDown() ) {
//            System.out.println("Bot went down for: " + downTimeLeft);
            return;
        }

        // Grab a random known peer
        LinkedList<PeerBlock> peersList = (LinkedList<PeerBlock>) peers;
        int index = rng.nextInt(peers.size());
        PeerBlock peerBlock = peersList.get(index);

        index = rng.nextInt(peerBlock.getPeers().size());
        ZeroAccessBot peer = peerBlock.getPeers().get(index);
        // Add its known peers to own peer list, replacing existing peers
        PeerBlock receivedPeers = peer.knownPeers(this);
        if(!peers.contains(receivedPeers)){
            peers.add(receivedPeers);
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

    public void setPeers(Deque<PeerBlock> peers) {
        this.peers = peers;
    }

    public Deque<PeerBlock> getPeers() {
        return peers;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

}
