import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Patrick on 12/8/2014.
 *
 * This class provides methods to simulate the publishing and verification of a signed block.
 * While an actual botnet would not have an external lookup table for valid blocks,
 * it would have a way of verifying whether a block came from the botmaster or not.
 */
public class BlockManager {

    List<PeerBlock> blockList;
    List<ZeroAccessBot> botList;

    public static final int BLOCK_SIZE = 16;

    int blockNumber = 0;

    static BlockManager instance = new BlockManager();
    public static BlockManager getInstance() {return instance;}



    private BlockManager(){
        blockList = new ArrayList<PeerBlock>();
        botList = new ArrayList<ZeroAccessBot>();
    }

    public void register(ZeroAccessBot b){
        if(!botList.contains(b)) {
            botList.add(b);
        }
    }

    public PeerBlock publish(){
        List<ZeroAccessBot> blockBots = new ArrayList<ZeroAccessBot>();
        //If fewer bots exist than BLOCK_SIZE, get the whole list
        if(botList.size() < BLOCK_SIZE){
            Collections.copy(blockBots, botList);
        }else{
            Collections.shuffle(botList);
            for(int i = 0; i<BLOCK_SIZE; i++){
                blockBots.add(botList.get(i));
            }
        }

        //Keep a reference to the block for verification
        PeerBlock b;

        if(blockNumber++ < 10000){
            b = new PeerBlock(0, blockBots);
        }else {
            b = new PeerBlock( (blockNumber++ - 10000), blockBots);
        }
        blockList.add(b);

        return b;

    }

    public boolean verify(PeerBlock block){
        return blockList.contains(block);
    }

}
