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
            List<ZeroAccessBot> peers = bot.knownPeers(this);
            for (ZeroAccessBot peer : peers) {
                if (!discoveredBots.contains(peer)) {
                    newBots.add(peer);
                }
            }
        }
        discoveredBots.addAll(newBots);

        super.tick();
    }

    @Override
    public void adoptPeer(ZeroAccessBot newBot) {
        discoveredBots.add(newBot);
        super.adoptPeer(newBot);
    }

    public void setPeers(Deque<ZeroAccessBot> peers) {
        for (ZeroAccessBot peer : peers) {
            discoveredBots.add(peer);
        }
        super.setPeers(peers);
    }

    @Override
    public Collection<ZeroAccessBot> getEnumeratedBots() {
        return discoveredBots;
    }
}
