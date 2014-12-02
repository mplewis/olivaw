import java.util.*;

public class ZeroAccess {

    public static final boolean DEBUG_ADOPTIONS = false;

    // Each bot has 16 random peers on creation
    public static final int INITIAL_BOTS = 10000;
    public static final int INITIAL_PEERS = 16;

    // 1 in 10 bots will adopt newly-created bots into their own peer list
    public static final int CHANCE_OF_ADOPTION = 10;

    // One tick = 256 seconds = 4m12s
    public static final int SIM_TICKS = 338*2;  // 338 ticks ~= 24 hours
    public static final int NEW_VER_EVERY = 14;  // 14 ticks ~= 1 hour
    public static final int NEW_BOT_EVERY = 1;  // New bot every 256 ticks

    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Create the net
        ZeroAccessNet net = new ZeroAccessNet();

        // Create INITIAL_BOTS initial bots
        List<ZeroAccessBot> initialBots = new ArrayList<ZeroAccessBot>();
        for (int i = 0; i < INITIAL_BOTS; i++) {
            ZeroAccessBot bot = new GoodBot();
            initialBots.add(bot);
        }
        for (int i = 0; i < 4; i++) {
            initialBots.add(new PartitionBot());
        }

        // Give each bot INITIAL_PEERS peers to start
        for (ZeroAccessBot bot : initialBots) {
            Deque<ZeroAccessBot> newPeers = new LinkedList<ZeroAccessBot>();
            for (int i = 0; i < INITIAL_PEERS; i++) {
                ZeroAccessBot randomBot = initialBots.get(rng.nextInt(initialBots.size()));
                newPeers.add(randomBot);
            }
            bot.setPeers(newPeers);
        }

        net.setBots(initialBots);

        List<EnumerationBot> enumerationBots = new ArrayList<EnumerationBot>();

        // Run the net
        int latestVersion = 0;
        for (int tick = 0; tick < SIM_TICKS; tick++) {
            // Every NEW_VER_EVERY ticks, give a random bot a newly-released version
            if (net.getTicks() % NEW_VER_EVERY == 0) {
                latestVersion++;
                ZeroAccessBot randomBot = initialBots.get(rng.nextInt(initialBots.size()));
                randomBot.setVersion(latestVersion);
            }

            // Every NEW_BOT_EVERY ticks, add a new bot to the net with n initial peers
            if (net.getTicks() % NEW_BOT_EVERY == 0) {
                ZeroAccessBot newBot;
                int whichBot = rng.nextInt(50);
                if (false && whichBot < 1) {
                    newBot = new SensorBot();
                    enumerationBots.add((EnumerationBot) newBot);
                } else {
                    newBot = new GoodBot();
                }
                
                List<ZeroAccessBot> allBots = net.getBots();
                Deque<ZeroAccessBot> newPeers = new LinkedList<ZeroAccessBot>();
                for (int i = 0; i < INITIAL_PEERS; i++) {
                    ZeroAccessBot randomBot = allBots.get(rng.nextInt(allBots.size()));
                    newPeers.add(randomBot);
                }
                newBot.setPeers(newPeers);
                allBots.add(newBot);
                net.setBots(allBots);

                // 1 in 10 bots adopts the new bot into its peer list
                int adoptionCount = 0;
                for (ZeroAccessBot adoptingBot : allBots) {
                    if (rng.nextInt(CHANCE_OF_ADOPTION) == 0) {
                        adoptingBot.adoptPeer(newBot);
                        adoptionCount++;
                    }
                }
                if (DEBUG_ADOPTIONS) {
                    System.out.println("    New bot adopted by " + adoptionCount + " bots");
                }
            }

            net.tick();
        }

        // Report from sensor bots
        Set<ZeroAccessBot> enumeratedBots = new HashSet<ZeroAccessBot>();
        for (EnumerationBot enumerator : enumerationBots) {
            enumeratedBots.addAll(enumerator.getEnumeratedBots());
        }
        for (EnumerationBot enumerator : enumerationBots) {
            enumeratedBots.remove(enumerator);
        }
        System.out.println("Number of enumerators: " + enumerationBots.size());
        System.out.println("Enumerated " + enumeratedBots.size() + " bots of " + net.getBots().size());
    }

}
