package src;

import java.util.Deque;
import java.util.List;

public class BadBot implements ZeroAccessBot{

	static int PEERS_TO_SEND = 16;
	
	public BadBot(){
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
	
	
}