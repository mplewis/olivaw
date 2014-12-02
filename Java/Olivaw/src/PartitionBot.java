import java.util.*;

public class PartitionBot extends GoodBot {

    private Map<ZeroAccessBot,Set<ZeroAccessBot>> assumedPeerLists = new HashMap<ZeroAccessBot,HashSet<ZeroAccessBot>>
    
    protected class BotCountPair implements Comparable {
        private int count;
        private ZeroAccessBot bot;
        
        public BotCountPair(ZeroAccessBot bot, int count) {
            this.bot = bot;
            this.count = count;
        }
        
        @Override
        public int compareTo(Object o) {
            BotCountPair otherPair = (BotCountPair) o;
            return otherPair.count - this.count; // inverts order, b/c PriorityQ is least
        }
    }
    
    // calculates number of shared peers based on assumedPeerLists
    private int sharedPeers(ZeroAccessBot a, ZeroAccessBot b) {
        Set<ZeroAccessBot> apeers = assumedPeerLists.get(a);
        Set<ZeroAccessBot> bpeers = assumedPeerLists.get(b);
        int count = 0;
        
        for ( ZeroAccessBot apeer : apeers ) {
            if ( bpeers.contains(apeer) ) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        Set<ZeroAccessBot> callerPeers = assumedPeerLists.get(caller);
        PriorityQueue<BotCountPair> queue = new PriorityQueue<BotCountPair>();
        
        for ( ZeroAccessBot peer : callerPeers ) {
            Set<ZeroAccessBot> peersOfPeer = assumedPeerLists.get(peer);
            for ( ZeroAccessBot peerOfPeer : peersOfPeer ) {
                if ( !queue.contains(peerOfPeer) ) {
                    queue.add(new BotCountPair(peerOfPeer, sharedPeers(caller,peerOfPeer)));
                }
            }
        }
        
        List<ZeroAccessBot> toReturn = new ArrayList<ZeroAccessBot>();
        for ( int i = 0; i < PEERS_TO_RETURN && !queue.isEmpty(); i++ ) {
            toReturn.add(queue.remove());
        }
        return toReturn;
    }
    
    private void mergeInAssumedPeers(ZeroAccessBot bot, List<ZeroAccessBot> new peers) {
        assumedPeerLists.get(bot).addAll(peers); // TODO update to limit to 256
    }
    
    @Override
    public void tick() {
        Set<ZeroAccessBot> newBots = new HashSet<ZeroAccessBots>();
        Set<ZeroAccessBot> knownBots = assumedPeerLists.keySet();
        
        for ( ZeroAccessBot peer : knownBots ) {
            List<ZeroAccessBot> peersOfPeer = peer.knownPeers(this);
            mergeInAssumedPeers(peer, peersOfPeer);
            for ( ZeroAccessBot peerOfPeer : peersOfPeer ) {
                if ( !knownBots.contains(peerOfPeer) ) {
                    newBots.add(peerOfPeer);
                }
            }
        }
        
        for ( ZeroAccessBot bot : newBots ) {
            assumedPeerLists.put(bot, new HashSet<ZeroAccessBot>());
        }
    }
    
    @Override
    public void setPeers(Deque<ZeroAccessBot> peers) {
        for ( ZeroAccessBot peer : peers ) {
            if ( assumedPeerLists.get(peer) == null ) {
                assumedPeerLists.put(peer, new HashSet<ZeroAccessBot>());
            }
        }
    }
}
