import java.util.*;

public class ZeroAccess {

    public static final boolean DEBUG_ADOPTIONS = false;

    // Each bot has 16 random peers on creation
    public static final int INITIAL_BOTS = 10000;
    public static final int INITIAL_PEER_BLOCKS = 1;
    public static final int SEED_BOTS_PER_VERSION_COUNT = 8;
    public static final int SEED_BOTS_POOL_SIZE = 64;

    // 1 in 10 bots will adopt newly-created bots into their own peer list
    public static final int CHANCE_OF_ADOPTION = 100;

    // One tick = 256 seconds = 4m12s
    public static final int SIM_TICKS = 338*14;  // 338 ticks ~= 24 hours
    public static final int NEW_VER_EVERY = 14;  // 14 ticks ~= 1 hour
    public static final int NEW_BOT_EVERY = 1;  // New bot every 256 ticks
    public static final int NEW_BLOCK_EVERY = 1;

    public static final int BLOCK_PUBLISH_NUMBER = 4;

    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Create the net
        ZeroAccessNet net = new ZeroAccessNet();

        List<EnumerationBot> enumerationBots = new ArrayList<EnumerationBot>();

        // Create INITIAL_BOTS initial bots
        List<ZeroAccessBot> seedBots = new ArrayList<ZeroAccessBot>();
        for (int i = 0; i < SEED_BOTS_POOL_SIZE; i++) {
            ZeroAccessBot bot = new GoodBot();
            seedBots.add(bot);
        }
        List<ZeroAccessBot> initialBots = new ArrayList<ZeroAccessBot>(seedBots);
        for (int i = 0; i < INITIAL_BOTS-SEED_BOTS_POOL_SIZE; i++) {
            ZeroAccessBot bot = new GoodBot();
            initialBots.add(bot);
        }
        for (int i = 0; i < 5; i++) {
//            ZeroAccessBot bot = new SensorBot();
//            initialBots.add(bot);
//            enumerationBots.add((EnumerationBot) bot);
        }

        // Give each bot INITIAL_PEERS peers to start
        BlockManager manager = BlockManager.getInstance();
        for (ZeroAccessBot bot : initialBots) {
            Deque<PeerBlock> newPeers = new LinkedList<PeerBlock>();
            for (int i = 0; i < INITIAL_PEER_BLOCKS; i++) {
                newPeers.add(manager.publish());
            }
            bot.setPeers(newPeers);
        }

        net.setBots(initialBots);

        // Run the net
        int latestVersion = 0;
        for (int tick = 0; tick < SIM_TICKS; tick++) {
            // Every NEW_VER_EVERY ticks, give a random bot a newly-released version
            if (net.getTicks() % NEW_VER_EVERY == 0) {
                latestVersion++;
                for (int i = 0; i < SEED_BOTS_PER_VERSION_COUNT; i++) {
                    ZeroAccessBot randomBot = seedBots.get(rng.nextInt(seedBots.size()));
                    randomBot.setVersion(latestVersion);
                }
            }

            // Every NEW_BOT_EVERY ticks, add a new bot to the net with n initial peers
            if (net.getTicks() % NEW_BOT_EVERY == 0) {
                ZeroAccessBot newBot = new GoodBot();

                List<ZeroAccessBot> allBots = net.getBots();
                Deque<PeerBlock> newPeers = new LinkedList<PeerBlock>();
                for (int i = 0; i < INITIAL_PEER_BLOCKS; i++) {
                    newPeers.add(manager.publish());
                }
                newBot.setPeers(newPeers);
                allBots.add(newBot);
                net.setBots(allBots);

                // 1 in 10 bots adopts the new bot into its peer list
//                int adoptionCount = 0;
//                for (ZeroAccessBot adoptingBot : allBots) {
//                    if (rng.nextInt(CHANCE_OF_ADOPTION) == 0) {
//                        adoptingBot.adoptPeer(newBot);
//                        adoptionCount++;
//                    }
//                }
//                if (DEBUG_ADOPTIONS) {
//                    System.out.println("    New bot adopted by " + adoptionCount + " bots");
//                }
            }
            // Every NEW_BLOCK_EVERY ticks, add BLOCK_PUBLISH_NUMBER blocks to the system
            if (net.getTicks() % NEW_BLOCK_EVERY == 0){
                List<ZeroAccessBot> allBots = net.getBots();
                for(int i = 0; i < BLOCK_PUBLISH_NUMBER; i++){
                    allBots.get(0).acceptBlockFromMaster(manager.publish());
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
