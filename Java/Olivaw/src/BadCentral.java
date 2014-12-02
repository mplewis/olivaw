package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BadCentral{
	
	private static BadCentral instance = new BadCentral();
	
	public static BadCentral getInstance() {return instance;}
	
	List<ZeroAccessBot> compromisedBots;
	
	private BadCentral(){
		compromisedBots = new ArrayList<ZeroAccessBot>();
	}
	
	public void submit(ZeroAccessBot b){
		if(!compromisedBots.contains(b)){
			compromisedBots.add(b);
		}
	}
	
	public List<ZeroAccessBot> getBots(int n){
		List<ZeroAccessBot> retBots = new ArrayList<>();
		if(compromisedBots.size() < n){
			Collections.copy(compromisedBots, retBots);
			return retBots;
		}else
		{
			Random rng = new Random();
			
			while(n-- > 0){
				int index = rng.nextInt(compromisedBots.size());
				retBots.add(compromisedBots.get(index));
			}
		}
		return retBots;
	}
	
}