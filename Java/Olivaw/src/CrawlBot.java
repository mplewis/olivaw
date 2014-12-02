import java.util.*;

public class CrawlBot extends GoodBot {

    private Set<ZeroAccessBot> discoveredBots = new HashSet<ZeroAccessBot>();
    private Set<ZeroAccessBot> newBots = new HashSet<ZeroAccessBot>();

    @Override
    public void tick() {

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
}
