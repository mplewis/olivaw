import java.util.*;

public class ZeroAccessNet {

    private List<ZeroAccessBot> bots = new ArrayList<ZeroAccessBot>();
    private Map<Integer, Integer> lastVersionCount = new HashMap<Integer, Integer>();
    private final double startTime;
    private long ticks = 0;
    private double cumulativeAverageVersionLag = 0.0;

    public ZeroAccessNet() {
        startTime = System.nanoTime();
    }

    private static double versionLag(Map<Integer,Integer> versionCount, int latestVersion) {
        double totalBots = 0.0;
        double accumulator = 0.0;
        for (Integer version : versionCount.keySet()) {
            if ( version.intValue() > -1 ) {
                int population = versionCount.get(version).intValue();
                totalBots += population;
                accumulator += version.intValue()*population;
            }
        }
        return latestVersion - accumulator/totalBots;
    }

    public void tick() {
        // Increment tick count, shuffle bots, and tick all bots. Core functionality!
        ticks++;
        Collections.shuffle(bots);
        for (ZeroAccessBot bot : bots) {
            bot.tick();
        }

        // Tally bot version counts
        Map<Integer, Integer> versionCount = new HashMap<Integer, Integer>();
        for (ZeroAccessBot bot : bots) {
            int v = bot.getVersion();
            if (!versionCount.containsKey(v)) {
                versionCount.put(v, 1);
            } else {
                int count = versionCount.get(v);
                versionCount.put(v, count + 1);
            }
        }

        // Get latest version
        List<Integer> allVersions = new ArrayList<Integer>(versionCount.keySet());
        Collections.sort(allVersions);
        int latestVersion = allVersions.get(allVersions.size() - 1);

        // If any bots have changed version, print the new version counts
        if (!lastVersionCount.equals(versionCount)) {
            double now = (System.nanoTime() - startTime) / 1000000000; // nanoseconds as seconds
//            double saturation = (double)versionCount.get(latestVersion)/bots.size();
//            cumulativeAverageSaturation = ((cumulativeAverageSaturation*ticks)+saturation)/(ticks+1);
//            double versionLag = versionLag(versionCount,latestVersion);
//            cumulativeAverageVersionLag = ((cumulativeAverageVersionLag*ticks)+versionLag)/(ticks+1);
            System.out.println(String.format("%.2f %s %s %s",// %.4f %.4f",
                    now,
                    ticks,
                    latestVersion,
                    versionCount
//                    versionLag,
//                    cumulativeAverageVersionLag
            ));
        }
        lastVersionCount = versionCount;
    }

    public long getTicks() {
        return ticks;
    }

    public List<ZeroAccessBot> getBots() {
        return bots;
    }

    public void setBots(List<ZeroAccessBot> bots) {
        this.bots = bots;
    }

    public Map<Integer, Integer> getLastVersionCount() {
        return lastVersionCount;
    }

    public void setLastVersionCount(Map<Integer, Integer> lastVersionCount) {
        this.lastVersionCount = lastVersionCount;
    }

}
