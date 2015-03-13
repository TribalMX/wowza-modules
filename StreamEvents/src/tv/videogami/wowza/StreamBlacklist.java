package tv.videogami.wowza;

import java.util.HashMap;
import java.util.Map;

public class StreamBlacklist {

	public static Map<String, Boolean> blacklist = new HashMap<String, Boolean>();
	
	public static void put(String streamName, boolean blacklisted){
		blacklist.put(streamName, blacklisted);
	}
	
	public static void remove(String streamName){
		blacklist.remove(streamName);
	}
	
	public static boolean isBlacklisted(String streamName){
		return blacklist.get(streamName);
	}
}
