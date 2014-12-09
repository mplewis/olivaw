import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Patrick on 12/8/2014.
 */
public class PeerBlock {

    private int orderNumber;
    private List<ZeroAccessBot> peerList;

    public PeerBlock(int number, List<ZeroAccessBot> p){
        orderNumber = number;
        peerList = p;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }

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

    public List<ZeroAccessBot> getPeers(){
        List<ZeroAccessBot> retList = new ArrayList<ZeroAccessBot>();
        for(ZeroAccessBot bot : peerList){
            retList.add(bot);
        }
        return retList;
    }

    public int getBlockOrderNumber() {
        return orderNumber;
    }
}
