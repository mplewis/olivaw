import java.util.*;

public class ZeroAccessNet {

    private List<ZeroAccessBot> bots = new ArrayList<ZeroAccessBot>();
    private Map<Integer, Integer> lastVersionCount = new HashMap<Integer, Integer>();
    private final double startTime;
    private long ticks = 0;

    public ZeroAccessNet() {
        startTime = System.nanoTime();
    }

    public void tick() {
        ticks++;
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
        List<Integer> allVersions = new ArrayList<Integer>(versionCount.keySet());
        Collections.sort(allVersions);
        int latestVersion = allVersions.get(allVersions.size() - 1);
        if (!lastVersionCount.equals(versionCount)) {
            double now = System.nanoTime() - startTime;
            System.out.println(String.format("%s %s %s %s",
                    now,
                    ticks,
                    latestVersion,
                    versionCount
            ));
        }
        lastVersionCount = versionCount;
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
