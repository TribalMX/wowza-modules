package tv.videogami.wowza;

import java.util.List;
import java.util.Map;

import tv.videogami.utils.Utils;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.http.*;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.MediaStreamMap;
import com.wowza.wms.vhost.*;

public class ForceStreamHTTPProvider extends HTTProvider2Base {

	private static final String STREAM = "stream";
	private static final String ACTION = "action";
	private static final String BLOCK = "block";
	
	public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {
		if (!doHTTPAuthentication(vhost, req, resp))
			return;

		try {
			String msg = "a:1";
			resp.getOutputStream().write(msg.getBytes());
		} catch (Exception e) {
			Utils.log("ERROR force stop stream response e:" + e.toString());
		}
		
		Map<String, List<String>> params = req.getParameterMap();
		String streamName = null;
		String action = null;
		String block = null;
		if (req.getMethod().equalsIgnoreCase("post"))
			req.parseBodyForParams(true);
		if (params.containsKey(STREAM))
			streamName = params.get(STREAM).get(0);
		if (params.containsKey(ACTION))
			action = params.get(ACTION).get(0);
		if (params.containsKey(BLOCK))
			block = params.get(BLOCK).get(0);
				
		Utils.log("INFO force stopping stream:" + streamName + " action:" + action + " block:" + block);
		
		StreamBlacklist.put(streamName, "true".equalsIgnoreCase(block));
		Utils.log("INFO stream blacklist:" + StreamBlacklist.blacklist.toString());

		try {
			IApplicationInstance appInstance = vhost.getApplication("live").getAppInstance("_definst_");
			MediaStreamMap streamMap = appInstance.getStreams();					
			IMediaStream stream = streamMap.getStream(streamName);
			if (stream == null){
				Utils.log("WARNING force stop stream doesnt exist stream:" + streamName + " action:" + action + " block:" + block);
				return;
			}
			IClient client = stream.getClient();
			client.rejectConnection();
			client.setShutdownClient(true);	
			Utils.log("INFO force stopped stream:" + streamName + " action:" + action + " block:" + block);
		} catch (Exception e) {
			Utils.log("ERROR force stop stream:" + streamName + " action:" + action + " block:" + block + " e:" + e.toString());
		}
	}
}
