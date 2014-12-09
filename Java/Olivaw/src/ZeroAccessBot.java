
import java.util.Deque;

import java.util.List;

public abstract class ZeroAccessBot {

    public abstract PeerBlock knownPeers(ZeroAccessBot caller);

    public abstract void tick();

    public abstract void setVersion(int version);

    public abstract int getVersion();

    public abstract void setPeers(Deque<PeerBlock> peers);

    public abstract Deque<PeerBlock> getPeers();

    public abstract int maxPeerCount();

    //public abstract void adoptPeer(ZeroAccessBot newBot);

    public ZeroAccessBot(){
        BlockManager.getInstance().register(this);
    }

    public abstract void acceptBlockFromMaster(PeerBlock p);
}
