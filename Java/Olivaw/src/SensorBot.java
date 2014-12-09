import java.util.*;

public class SensorBot extends EnumerationBot {

    private static List<ZeroAccessBot> sensedPeers = new ArrayList<ZeroAccessBot>();

    @Override
    public PeerBlock knownPeers(ZeroAccessBot caller) {
        if (!sensedPeers.contains(caller)) {
            sensedPeers.add(caller);
        }
        return super.knownPeers(this);
    }

    @Override
    public Collection<ZeroAccessBot> getEnumeratedBots() {
        return sensedPeers;
    }
}
