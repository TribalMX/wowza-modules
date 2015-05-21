package tv.videogami.wowza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.videogami.utils.Utils;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.dvr.IDvrConstants;
import com.wowza.wms.dvr.IDvrStreamManager;
import com.wowza.wms.http.*;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.MediaStreamMap;
import com.wowza.wms.stream.live.ILiveReceiver;
import com.wowza.wms.stream.livedvr.ILiveStreamDvrRecorder;
import com.wowza.wms.vhost.*;

public class ClearDVRHTTPProvider extends HTTProvider2Base {

	private static final String STREAM = "stream";
	private static HashMap<String, ILiveStreamDvrRecorder> recordersMap = new HashMap<String, ILiveStreamDvrRecorder>();

	public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {
		if (!doHTTPAuthentication(vhost, req, resp))
			return;

		try {
			String msg = "a:1";
			resp.getOutputStream().write(msg.getBytes());
		} catch (Exception e) {
			Utils.log("ERROR clear DVR response e:" + e.toString());
		}

		Map<String, List<String>> params = req.getParameterMap();
		String streamName = null;
		if (req.getMethod().equalsIgnoreCase("post"))
			req.parseBodyForParams(true);
		if (params.containsKey(STREAM))
			streamName = params.get(STREAM).get(0);
		Utils.log("INFO clear DVR stream:" + streamName);

		try {
//			IApplicationInstance appInstance = vhost.getApplication("live").getAppInstance("_definst_");
//			MediaStreamMap streamMap = appInstance.getStreams();
//
//			IMediaStream stream = streamMap.getStream(streamName);
//			if (stream == null) {
//				Utils.log("WARNING clear DVR: stream not found:" + streamName);
//				return;
//			}
//			
//			ILiveStreamDvrRecorder dvrRecorder = stream.getDvrRecorder(IDvrConstants.DVR_DEFAULT_RECORDER_ID);
			
			ILiveStreamDvrRecorder dvrRecorder = getRecorder(streamName);
			if (dvrRecorder == null) {
				Utils.log("ERROR. clear DVR: dvr recorder not found: stream:" + streamName);
				return;
			}
			
			if (dvrRecorder.isRecording()) {
				Utils.log("INFO. clear DVR: dvr stop recording stream:" + streamName);
				dvrRecorder.stopRecording();
			}

			Utils.log("INFO. clear DVR: shutting down dvr recorder stream:" + streamName);
			dvrRecorder.shutdown();

			Utils.log("INFO. clear DVR: removing DVR store stream:" + streamName);
			IDvrStreamManager dvrManager = dvrRecorder.getDvrManager();
			if (dvrManager == null) {
				Utils.log("ERROR. clear DVR: dvr manager not found stream:" + streamName);
				return;
			}
			dvrManager.removeStreamStore(streamName);
		} catch (Exception e) {
			Utils.log("ERROR clear DVR stream:" + streamName + " e:" + e.toString());
		}
		
	}
	
	public static void addRecorder(String streamName, ILiveStreamDvrRecorder newRecorder){
		recordersMap.put(streamName, newRecorder);
	}

	public static void addRecorder(IMediaStream stream){
		String streamName = stream.getName();
		ILiveStreamDvrRecorder recorder = stream.getDvrRecorder(IDvrConstants.DVR_DEFAULT_RECORDER_ID);
		Utils.log("DEBUG. adding recorder stream:" + streamName + " recorder:" + recorder.toString());
		recordersMap.put(streamName, recorder);
	}
	
	public static ILiveStreamDvrRecorder getRecorder(String streamName){
		Utils.log("DEBUG. getting recorder stream:" + streamName + " recorder:" + recordersMap.get(streamName).toString());
		return recordersMap.get(streamName);
	}

	public static void removeRecorder(String streamName){
		recordersMap.remove(streamName);
	}

}
