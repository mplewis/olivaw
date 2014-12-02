import java.util.*;

public class SensorBot extends GoodBot {

    private List<ZeroAccessBot> sensedPeers = new ArrayList<ZeroAccessBot>();

    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        if (!sensedPeers.contains(caller)) {
            sensedPeers.add(caller);
        }
        return super.knownPeers(this);
    }
}
