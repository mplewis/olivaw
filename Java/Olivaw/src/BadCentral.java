import java.util.*;

public class BadCentral {

    private static BadCentral instance = new BadCentral();

    public static BadCentral getInstance() {
        return instance;
    }

    List<ZeroAccessBot> compromisedBots;
    HashMap<Integer, List<PeerBlock>> activeBlocks; // Key: # of bad bots in blocks, value: blocks

    private BadCentral() {
        compromisedBots = new ArrayList<ZeroAccessBot>();
        activeBlocks = new HashMap<Integer, List<PeerBlock>>();
        activeBlocks.put(0, new ArrayList<PeerBlock>());
    }

    public void submit(ZeroAccessBot b) {
        if (!compromisedBots.contains(b)) {
            compromisedBots.add(b);
        }
    }

    public PeerBlock getBlock(){
        return null;
    }

    public List<ZeroAccessBot> getBots(int n) {
        List<ZeroAccessBot> retBots = new ArrayList<ZeroAccessBot>();
        if (compromisedBots.size() < n) {
            Collections.copy(compromisedBots, retBots);
            return retBots;
        } else {
            Random rng = new Random();

            while (n-- > 0) {
                int index = rng.nextInt(compromisedBots.size());
                retBots.add(compromisedBots.get(index));
            }
        }
        return retBots;
    }

    public void giveBlocks(Deque<PeerBlock> peers) {
        LinkedList<PeerBlock> peerBlocks = (LinkedList<PeerBlock>) peers;
        //TODO analysis
        for(PeerBlock block : peerBlocks) {
            activeBlocks.get(0).add(block);

        }
    }

    public void giveBlocks(PeerBlock peers) {
        activeBlocks.get(0).add(peers);
    }
}