import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 12/8/2014.
 */
public class PeerBlock {

    int orderNumber;
    List<ZeroAccessBot> peerList;

    public PeerBlock(int number, List<ZeroAccessBot> p){
        orderNumber = number;
        peerList = p;
    }

    @Override
    public boolean equals(Object o){
        PeerBlock other = (PeerBlock) o;
        if(other.orderNumber != this.orderNumber){
            return false;
        }
        for(ZeroAccessBot bot : peerList){
            if(!other.peerList.contains(bot)){
                return false;
            }
        }
        if(peerList.size() != other.peerList.size()){
            return false;
        }
        return true;
    }

}
