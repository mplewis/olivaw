package src;

import java.util.List;

public class BadBot implements ZeroAccessBot{

	static int PEERS_TO_SEND = 16;
	
	public BadBot(){
		BadCentral.getInstance().submit(this);
	}
	
	@Override
	public List<ZeroAccessBot> knownPeers() {
		return BadCentral.getInstance().getBots(PEERS_TO_SEND);
	}

	@Override
	public void tick() {
		//nothing!
	}

	@Override
	public int getVersion() {
		return -1;
	}
	
	
}