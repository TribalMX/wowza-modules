package tv.videogami.utils;

import com.wowza.wms.logging.WMSLoggerFactory;

public class Utils {

	public static void log(String msg) {
		WMSLoggerFactory.getLogger(null).info("WASA " + msg);
	}
}
