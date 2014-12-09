import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class BadBot extends ZeroAccessBot {

    static int PEERS_TO_SEND = 16;

    public BadBot() {
        super();
        BadCentral.getInstance().submit(this);
    }

    @Override
    public void acceptBlockFromMaster(PeerBlock p) {
        BadCentral.getInstance().giveBlocks(p);
    }


    @Override
    public void tick() {
        //nothing!
    }

    @Override
    public int getVersion() {
        return -1;
    }

    @Override
    public PeerBlock knownPeers(ZeroAccessBot caller) {
        return BadCentral.getInstance().getBlock();
    }

    @Override
    public void setVersion(int version) {
        //nah
    }

    @Override
    public void setPeers(Deque<PeerBlock> peers) {
        BadCentral.getInstance().giveBlocks(peers);
    }

    @Override
    public Deque<PeerBlock> getPeers() {
        return new LinkedList<PeerBlock>();
    }

    @Override
    public int maxPeerCount() {
        return 256; // I'm lying
    }



}