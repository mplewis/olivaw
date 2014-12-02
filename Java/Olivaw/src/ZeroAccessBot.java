import java.util.List;

public interface ZeroAccessBot {

    public List<ZeroAccessBot> knownPeers();
    public void tick();
    public int getVersion();

}
