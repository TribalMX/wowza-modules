package tv.videogami.wowza;

import tv.videogami.utils.Request;

import com.wowza.wms.application.*;
import com.wowza.wms.amf.*;
import com.wowza.wms.client.*;
import com.wowza.wms.dvr.*;
import com.wowza.wms.module.*;
import com.wowza.wms.request.*;
import com.wowza.wms.stream.*;
import com.wowza.wms.stream.livedvr.*;
import com.wowza.wms.rtp.model.*;
import com.wowza.wms.httpstreamer.model.*;
import com.wowza.wms.httpstreamer.cupertinostreaming.httpstreamer.*;
import com.wowza.wms.httpstreamer.smoothstreaming.httpstreamer.*;

public class StreamEvents extends ModuleBase {
	
//	public static final String WASA_URL = "http://ec2-54-221-196-249.compute-1.amazonaws.com";
	public static final String WASA_URL = "http://localhost:8080";
	
	private void log(String msg){
		getLogger().info("WASA " + msg);
	}
	
	public void doSomething(IClient client, RequestFunction function, AMFDataList params) {
		log("doSomething");
		sendResult(client, params, "Hello Wowza");
	}

	public void onAppStart(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/" + appInstance.getName();
		log("onAppStart: " + fullname);
	}

	public void onAppStop(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/" + appInstance.getName();
		log("onAppStop: " + fullname);
	}

	public void onConnect(IClient client, RequestFunction function, AMFDataList params) {
		log("onConnect: " + client.getClientId());
	}

	public void onConnectAccept(IClient client) {
		log("onConnectAccept: " + client.getClientId());
	}

	public void onConnectReject(IClient client) {
		log("onConnectReject: " + client.getClientId());
	}

	public void onDisconnect(IClient client) {
		log("onDisconnect: " + client.getClientId());
	}

	public void onStreamCreate(IMediaStream stream) {
		log("onStreamCreate:" + stream.getSrc() + " by:"+ stream.getClientId());
		IMediaStreamActionNotify actionNotify = new StreamListener();
		stream.addClientListener(actionNotify);
	}

	public void onStreamDestroy(IMediaStream stream) {
		log("onStreamDestroy: " + stream.getSrc());
	}

	public void onHTTPSessionCreate(IHTTPStreamerSession httpSession) {
		log("onHTTPSessionCreate: " + httpSession.getSessionId());
	}

	public void onHTTPSessionDestroy(IHTTPStreamerSession httpSession) {
		log("onHTTPSessionDestroy: " + httpSession.getSessionId());
	}

	public void onHTTPCupertinoStreamingSessionCreate(HTTPStreamerSessionCupertino httpSession) {
		log("onHTTPCupertinoStreamingSessionCreate: " + httpSession.getSessionId());
	}

	public void onHTTPCupertinoStreamingSessionDestroy(HTTPStreamerSessionCupertino httpSession) {
		log("onHTTPCupertinoStreamingSessionDestroy: " + httpSession.getSessionId());
	}

	public void onHTTPSmoothStreamingSessionCreate(HTTPStreamerSessionSmoothStreamer httpSession) {
		log("onHTTPSmoothStreamingSessionCreate: " + httpSession.getSessionId());
	}

	public void onHTTPSmoothStreamingSessionDestroy(HTTPStreamerSessionSmoothStreamer httpSession) {
		log("onHTTPSmoothStreamingSessionDestroy: " + httpSession.getSessionId());
	}

	public void onRTPSessionCreate(RTPSession rtpSession) {
		log("onRTPSessionCreate: " + rtpSession.getSessionId());
	}

	public void onRTPSessionDestroy(RTPSession rtpSession) {
		log("onRTPSessionDestroy: " + rtpSession.getSessionId());
	}

	public void onCall(String handlerName, IClient client, RequestFunction function, AMFDataList params) {
		log("onCall: " + handlerName);
	}

	class StreamListener implements IMediaStreamActionNotify2 {
		public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset) {
			log("onPlay");
		}

		public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket) {
			log("onMetaData");
		}

		public void onPauseRaw(IMediaStream stream, boolean isPause, double location) {
			log("onPauseRaw");
		}

		public void onSeek(IMediaStream stream, double location) {
			log("onSeek");
		}

		public void onStop(IMediaStream stream) {
			log("onStop By: " + stream.getClientId());
		}

		public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
			log("onPublish stream:" + streamName);
			int resCode = Request.notifyStreamStart(WASA_URL, streamName);
			log("notifyStreamStart stream:" + streamName + " code:" + resCode);
		}

		public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
			log("unpublishing stream:" + streamName);
			
			ILiveStreamDvrRecorder dvrRecorder = stream.getDvrRecorder(IDvrConstants.DVR_DEFAULT_RECORDER_ID);
            if (dvrRecorder == null) {
            	log("ERROR. dvr recorder not found stream:" + streamName);
                return;
            }            
            if (dvrRecorder.isRecording()) {
                log("INFO. dvr stop recording stream:" + streamName);
            	dvrRecorder.stopRecording(); // maybe this is all you need!
            }
            
//            // kinda drastic. might cause delay starting up recorder when new stream comes in
//            // log("INFO. shutting down dvr recorder stream:" + streamName);
//            // dvrRecorder.shutdown(); 
            
//            log("INFO. removing stream store stream:" + streamName);
//            IDvrStreamManager dvrManager = dvrRecorder.getDvrManager();
//            if (dvrManager == null) {
//                log("ERROR. dvr manager not found stream:" + streamName);
//                return;
//            }
//            dvrManager.removeStreamStore(streamName);
		}

		public void onPause(IMediaStream stream, boolean isPause, double location) {
			log("onPause");
		}
	}
}