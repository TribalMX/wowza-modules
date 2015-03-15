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
		Boolean listed = blacklist.get(streamName);
		if (listed == null) return false;
		return listed;
	}
}
