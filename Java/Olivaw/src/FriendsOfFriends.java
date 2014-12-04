import java.util.*;

public class FriendsOfFriends extends GoodBot {

    private static Map<ZeroAccessBot, Deque<ZeroAccessBot>> assumedPeerLists = new HashMap<ZeroAccessBot, Deque<ZeroAccessBot>>();
    private static final int MAX_ASSUMED_PEERLIST_SIZE = 16;

    protected class BotCountPair implements Comparable {
        public int count;
        public ZeroAccessBot bot;

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
        Collection<ZeroAccessBot> apeers = assumedPeerLists.get(a);
        Collection<ZeroAccessBot> bpeers = assumedPeerLists.get(b);
        int count = 0;

        if ( apeers == null || bpeers == null ) {
            return 0;
        }

        for (ZeroAccessBot apeer : apeers) {
            if (bpeers.contains(apeer)) {
                count++;
            }
        }

        return count;
    }
    
    private boolean botCountPairContains(Collection<BotCountPair> coll, ZeroAccessBot bot) {
        for (BotCountPair pair : coll) {
            if (pair.bot == bot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        Collection<ZeroAccessBot> callerPeers = assumedPeerLists.get(caller);
        if ( callerPeers == null || callerPeers.isEmpty() ) {
            return super.knownPeers(caller);
        }
        PriorityQueue<BotCountPair> queue = new PriorityQueue<BotCountPair>();

        for (ZeroAccessBot peer : callerPeers) {
            Collection<ZeroAccessBot> peersOfPeer = assumedPeerLists.get(peer);
            for (ZeroAccessBot peerOfPeer : peersOfPeer) {
                if (!botCountPairContains(queue,peerOfPeer)) {
                    queue.add(new BotCountPair(peerOfPeer, sharedPeers(caller, peerOfPeer)));
                }
            }
        }

        List<ZeroAccessBot> toReturn = new ArrayList<ZeroAccessBot>();
        for (int i = 0; i < PEERS_TO_RETURN-1 && !queue.isEmpty(); i++) {
            toReturn.add(queue.remove().bot);
        }
        toReturn.add(this); // keep the poison bot!
        return toReturn;
    }

    private void mergeInAssumedPeers(ZeroAccessBot bot, Collection<ZeroAccessBot> peers) {
        for (ZeroAccessBot peer : peers) {
            if (assumedPeerLists.get(bot).contains(peer)) {
                assumedPeerLists.get(bot).remove(peer);
            } else if (assumedPeerLists.get(bot).size() >= MAX_ASSUMED_PEERLIST_SIZE) {
                assumedPeerLists.get(bot).remove();
            }
            assumedPeerLists.get(bot).add(peer);
        }
    }

    @Override
    public void tick() {
        Collection<ZeroAccessBot> newBots = new HashSet<ZeroAccessBot>();

        /* update with peers found on last tick */
        for (ZeroAccessBot bot : this.peers) {
            if (!assumedPeerLists.keySet().contains(bot)) {
                newBots.add(bot);
            }
        }

        Set<ZeroAccessBot> knownBots = assumedPeerLists.keySet();

        for (ZeroAccessBot peer : knownBots) {
            Collection<ZeroAccessBot> peersOfPeer = peer.knownPeers(this);
            mergeInAssumedPeers(peer, peersOfPeer);
            for (ZeroAccessBot peerOfPeer : peersOfPeer) {
                if (!knownBots.contains(peerOfPeer)) {
                    newBots.add(peerOfPeer);
                }
            }
        }

        for (ZeroAccessBot bot : newBots) {
            assumedPeerLists.put(bot, new ArrayDeque<ZeroAccessBot>());
        }

        super.tick();
    }

    @Override
    public void adoptPeer(ZeroAccessBot newBot) {
        if (assumedPeerLists.get(newBot) == null) {
            assumedPeerLists.put(newBot, new ArrayDeque<ZeroAccessBot>());
        }
        super.adoptPeer(newBot);
    }

    @Override
    public void setPeers(Deque<ZeroAccessBot> peers) {
        for (ZeroAccessBot peer : peers) {
            if (assumedPeerLists.get(peer) == null) {
                assumedPeerLists.put(peer, new ArrayDeque<ZeroAccessBot>());
            }
        }
        super.setPeers(peers);
    }

    @Override
    public int getVersion() {
        return -1;
    }
}
