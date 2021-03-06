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
    public void tick() {
        //nothing!
    }

    @Override
    public int getVersion() {
        return -1;
    }

    @Override
    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller) {
        return BadCentral.getInstance().getBots(PEERS_TO_SEND);
    }

    @Override
    public void setVersion(int version) {
        //nah
    }

    @Override
    public void setPeers(Deque<ZeroAccessBot> peers) {
        //No
    }

    @Override
    public Deque<ZeroAccessBot> getPeers() {
        return new LinkedList<ZeroAccessBot>(BadCentral.getInstance().getBots(PEERS_TO_SEND));
    }

    @Override
    public int maxPeerCount() {
        return 256; // I'm lying
    }

    @Override
    public void adoptPeer(ZeroAccessBot newBot) {
        return; // No thanks.
    }


}