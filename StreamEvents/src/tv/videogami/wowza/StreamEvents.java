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

	public static final String VDGAMI_URL = "http://vdgami-1085381642.us-east-1.elb.amazonaws.com";

	// TODO put in Utils. extend ModuleBase to get getLogger
	private void log(String msg) {
		getLogger().info("WASA " + msg);
	}

	public void doSomething(IClient client, RequestFunction function,
			AMFDataList params) {
		log("doSomething");
		sendResult(client, params, "Hello Wowza");
	}

	public void onAppStart(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/"
				+ appInstance.getName();
		log("onAppStart: " + fullname);
	}

	public void onAppStop(IApplicationInstance appInstance) {
		String fullname = appInstance.getApplication().getName() + "/"
				+ appInstance.getName();
		log("onAppStop: " + fullname);
	}

	public void onConnect(IClient client, RequestFunction function,
			AMFDataList params) {
		log("onconnect clientID:" + client.getClientId());
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
		log("onStreamCreate:" + stream.getSrc() + " by:" + stream.getClientId());
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

	public void onHTTPCupertinoStreamingSessionCreate(
			HTTPStreamerSessionCupertino httpSession) {
		log("onHTTPCupertinoStreamingSessionCreate: "
				+ httpSession.getSessionId());
	}

	public void onHTTPCupertinoStreamingSessionDestroy(
			HTTPStreamerSessionCupertino httpSession) {
		log("onHTTPCupertinoStreamingSessionDestroy: "
				+ httpSession.getSessionId());
	}

	public void onHTTPSmoothStreamingSessionCreate(
			HTTPStreamerSessionSmoothStreamer httpSession) {
		log("onHTTPSmoothStreamingSessionCreate: " + httpSession.getSessionId());
	}

	public void onHTTPSmoothStreamingSessionDestroy(
			HTTPStreamerSessionSmoothStreamer httpSession) {
		log("onHTTPSmoothStreamingSessionDestroy: "
				+ httpSession.getSessionId());
	}

	public void onRTPSessionCreate(RTPSession rtpSession) {
		log("onRTPSessionCreate: " + rtpSession.getSessionId());
	}

	public void onRTPSessionDestroy(RTPSession rtpSession) {
		log("onRTPSessionDestroy: " + rtpSession.getSessionId());
	}

	public void onCall(String handlerName, IClient client,
			RequestFunction function, AMFDataList params) {
		log("onCall: " + handlerName);
	}

	class StreamListener implements IMediaStreamActionNotify2 {
		public void onPlay(IMediaStream stream, String streamName,
				double playStart, double playLen, int playReset) {
			log("onPlay");
		}

		public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket) {
			log("onMetaData");
		}

		public void onPauseRaw(IMediaStream stream, boolean isPause,
				double location) {
			log("onPauseRaw");
		}

		public void onSeek(IMediaStream stream, double location) {
			log("onSeek");
		}

		public void onStop(IMediaStream stream) {
			log("onStop By: " + stream.getClientId());
		}

		public void onPublish(IMediaStream stream, String streamName,
				boolean isRecord, boolean isAppend) {
			
			// TODO do not print out user's passwords!
			// TODO do not print out user's passwords!
			// TODO do not print out user's passwords!

			IClient client = stream.getClient();
			String username = client.getProperties().getPropertyStr("username");
			String password = client.getProperties().getPropertyStr("password");
			
			// TODO. query vdgami db in authprovider for stream, username, and
			// password combo. for now just checking that the username matches
			// the stream name for simplicity
			
			if (streamName != username) {
				log("INFO bad auth onpublish stream:" + streamName + " username:" + username + " password:" + password);
				client.setShutdownClient(true);
				sendClientOnStatusError(client, "NetConnection.Connect.Rejected", "Rejected Connection");
				return;
			}

			log("INFO publishing stream:" + streamName + " username:"
					+ username + " password:" + password);

			int resCode = Request.notifyStreamEvent(VDGAMI_URL + "/v3/stream/"
					+ streamName + "/status/true", streamName);
			log("info notifying stream start stream:" + streamName + " code:"
					+ resCode);
		}

		public void onUnPublish(IMediaStream stream, String streamName,
				boolean isRecord, boolean isAppend) {
			log("info unpublishing stream:" + streamName);
			int resCode = Request.notifyStreamEvent(VDGAMI_URL + "/v3/stream/"
					+ streamName + "/status/false", streamName);
			log("info notifying stream end stream:" + streamName + " code:"
					+ resCode);

			// // Seems you can just config this in conf/[live/]Application.xml
			// ILiveStreamDvrRecorder dvrRecorder =
			// stream.getDvrRecorder(IDvrConstants.DVR_DEFAULT_RECORDER_ID);
			// if (dvrRecorder == null) {
			// log("ERROR. dvr recorder not found stream:" + streamName);
			// return;
			// }
			// if (dvrRecorder.isRecording()) {
			// log("INFO. dvr stop recording stream:" + streamName);
			// dvrRecorder.stopRecording(); // maybe this is all you need!
			// }

			// // kinda drastic. might cause delay starting up recorder when new
			// stream comes in
			// // log("INFO. shutting down dvr recorder stream:" + streamName);
			// // dvrRecorder.shutdown();

			// // another possible solution
			// log("INFO. removing stream store stream:" + streamName);
			// IDvrStreamManager dvrManager = dvrRecorder.getDvrManager();
			// if (dvrManager == null) {
			// log("ERROR. dvr manager not found stream:" + streamName);
			// return;
			// }
			// dvrManager.removeStreamStore(streamName);
		}

		public void onPause(IMediaStream stream, boolean isPause,
				double location) {
			log("onPause");
		}
	}
}