import java.util.Deque;
import java.util.List;

public interface ZeroAccessBot {

    public List<ZeroAccessBot> knownPeers(ZeroAccessBot caller);
    public void tick();
    public void setVersion(int version);
    public int getVersion();
    public void setPeers(Deque<ZeroAccessBot> peers);

}
