import java.util.*;

public class CrawlBot extends EnumerationBot {

    private static Set<ZeroAccessBot> discoveredBots = new HashSet<ZeroAccessBot>();

    @Override
    protected boolean isDown() {
        return false;
    }

    @Override
    public void tick() {
        Set<ZeroAccessBot> newBots = new HashSet<ZeroAccessBot>();

        for (ZeroAccessBot bot : discoveredBots) {
            PeerBlock peerBlock = bot.knownPeers(this);
            List<ZeroAccessBot> peers = peerBlock.getPeers();
            for (ZeroAccessBot peer : peers) {
                if (!discoveredBots.contains(peer)) {
                    newBots.add(peer);
                }
            }
        }
        discoveredBots.addAll(newBots);

        super.tick();
    }
//
//    @Override
//    public void adoptPeer(ZeroAccessBot newBot) {
//        discoveredBots.add(newBot);
//        super.adoptPeer(newBot);
//    }

    public void setPeers(Deque<PeerBlock> peerBlocks) {
        for (PeerBlock peerBlock : peerBlocks) {
            List<ZeroAccessBot> peers = peerBlock.getPeers();
            for(ZeroAccessBot peer : peers) {
                discoveredBots.add(peer);
            }
        }
        super.setPeers(peers);
    }

    @Override
    public Collection<ZeroAccessBot> getEnumeratedBots() {
        return discoveredBots;
    }
}
