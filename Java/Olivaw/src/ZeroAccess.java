import java.util.*;

public class ZeroAccess {

    public static final int INITIAL_BOTS = 1000;
    public static final int INITIAL_PEERS = 16;
    public static final int SIM_TICKS = 60 * 60 * 24;  // 24 hours
    public static final int NEW_VER_EVERY = 60 * 60;  // 1 hour
    public static final int NEW_BOT_EVERY = 60 * 2;  // 2 minutes

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
                ZeroAccessBot bot = new GoodBot();
                List<ZeroAccessBot> allBots = net.getBots();
                Deque<ZeroAccessBot> newPeers = new LinkedList<ZeroAccessBot>();
                for (int i = 0; i < INITIAL_PEERS; i++) {
                    ZeroAccessBot randomBot = allBots.get(rng.nextInt(allBots.size()));
                    newPeers.add(randomBot);
                }
                bot.setPeers(newPeers);
            }

            net.tick();
        }
    }

}