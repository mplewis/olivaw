import java.util.*;

public class CrawlBot extends GoodBot {

    private Set<ZeroAccessBot> discoveredBots = new HashSet<ZeroAccessBot>();
    
    @Override
    public void tick() {
        private Set<ZeroAccessBot> newBots = new HashSet<ZeroAccessBots>();
        
        for ( ZeroAccessBot bot : discoveredBots ) {
            List<ZeroAccessBot> peers = bot.knownPeers(this);
            for ( ZeroAccessBot peer : peers ) {
                if ( !discoveredBots.contains(peer) ) {
                    newBots.add(peer);
                }
            }
        }
        discoveredBots.addAll(newBots);
        
        super.tick();
    }
}
