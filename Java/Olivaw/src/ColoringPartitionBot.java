import java.util.*;

/**
 * Created by kviratyosin on 12/2/14.
 */
public class ColoringPartitionBot extends GoodBot {

    private static final int NUM_COLORS = 8;
    private static Map<Integer,ArrayList<ZeroAccessBot>> partitions = new HashMap<Integer,ArrayList<ZeroAccessBot>>();

    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        ArrayList<ZeroAccessBot> peers = partitions.get(new Integer(caller.hashCode() % NUM_COLORS));
        if (peers == null || peers.isEmpty()) {
            return super.knownPeers(caller);
        }

        // If you don't have enough peers, return all of them
        if (peers.size() <= PEERS_TO_RETURN) {
            return new ArrayList<ZeroAccessBot>(peers);
        }

        // Otherwise, return PEERS_TO_RETURN peers
        ArrayList<ZeroAccessBot> remainingPeers = new ArrayList<ZeroAccessBot>(peers);
        ArrayList<ZeroAccessBot> selectedPeers = new ArrayList<ZeroAccessBot>();
        while (selectedPeers.size() < PEERS_TO_RETURN) {
            int index = rng.nextInt(remainingPeers.size());
            selectedPeers.add(remainingPeers.remove(index));
        }
        return selectedPeers;
    }

    @Override
    public void tick() {
        super.tick();

        for (ZeroAccessBot peer : this.peers) {
            Integer color = new Integer(peer.hashCode() % NUM_COLORS);
            if (partitions.get(color) == null) {
                partitions.put(color, new ArrayList<ZeroAccessBot>());
            }
            partitions.get(color).add(peer);
        }
    }
}
