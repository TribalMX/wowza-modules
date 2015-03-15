package tv.videogami.wowza;

import java.util.HashMap;
import java.util.Map;

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

//	public static final String VDGAMI_URL = "http://vdgami-1085381642.us-east-1.elb.amazonaws.com";
	// we now have a node server (again) on wowza that handles requests from wowza engine instead of the vdgami api server.
	// need to do that cause the wowza engine doesn't know if it's a production or development server, so it'll send requests
	// to the production vdgami api server instead of the dev env. OTOH, the node server on wowza knows whether it's
	// NODE_ENV=production|development
	public static final String VDGAMI_URL = "http://localhost:8080";

	// TODO put in Utils. extend ModuleBase to get getLogger
	private static void log(String msg) {
		getLogger().info("WASA " + msg);
	}

	// TODO put this in utils
	public static Map<String, String> getQueryMap(String query, String sep1, String sep2) {
		String[] params = query.split(sep1);
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			try {
				String name = param.split(sep2)[0];
				String value = param.split(sep2)[1];
				map.put(name, value);
			} catch (Exception e) {
				// don't do anything
			}
		}
		return map;
	}

	// TODO refactor
	public static boolean clientLogin(IClient client, String streamName) {
		String url = null;
		String qs = null;
		String streamname = null;
		String token = null;
		try {
			url = client.getUri(); // rtmp://localhost:1935/live?token=supercolada/flv:54f178d9d9a6ec840d29610f
			Map<String, String> urlmap = getQueryMap(url, "/", ":");
			// streamname = urlmap.get("flv"); // can't use this cause ffmpeg
			// doesn't give you this guy
			streamname = streamName;
			qs = client.getQueryStr(); // token=supercolada/flv:54f178d9d9a6ec840d29610f
			qs = qs.split("/")[0]; // token=supercolada&blah=something
			Map<String, String> qsmap = getQueryMap(qs, "&", "=");
			token = qsmap.get("token");
		} catch (Exception e) {
			// TODO send error message back to client
			log("ERROR client login invalid clientID:" + client.getClientId() + " url:" + url + "?" + qs + " e:" + e.toString());
			return false;
		}
		if (token == null || streamname == null) {
			log("ERROR client login null auth clientID:" + client.getClientId() + " url:" + url + "?" + qs);
			return false;
		}
		log("INFO client login clientID:" + client.getClientId() + " url:" + url + "?" + qs);

		String req = VDGAMI_URL + "/v3/streamerlogin/" + streamname + "/" + token;
		int res = Request.streamerLogin(req, streamname, token);
		if (res == -1) {
			log("ERROR client login exception clientID:" + client.getClientId() + " req:" + req + " res:" + res);
			return false;
		} else if (res == 200) {
			log("INFO client login success clientID:" + client.getClientId() + " req:" + req + " res:" + res);
			return true;
		} else { // using 404
			log("WARNING client login failed clientID:" + client.getClientId() + " req:" + req + " res:" + res);
			return false;
		}
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

	// TODO send error messages to FMLE client
	public void onConnect(IClient client, RequestFunction function, AMFDataList params) {
		log("INFO onconnect clientID:" + client.getClientId());
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

		private void shutdownClient(IClient client) {
			client.rejectConnection();
			client.setShutdownClient(true);
		}

		public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
			IClient client = stream.getClient();

			if (StreamBlacklist.isBlacklisted(streamName)){
				log("WARNING onpublish client stream blacklisted clientID:" + client.getClientId() + " stream:" + streamName);
				shutdownClient(client);
				return;
			}
			
			boolean re = clientLogin(client, streamName);
			if (!re) {
				log("WARNING onpublish client login failed clientID:" + client.getClientId() + " stream:" + streamName);
				shutdownClient(client);
				return;
			} else log("INFO onpublish client login success clientID:" + client.getClientId());

			int resCode = Request.notifyStreamEvent(VDGAMI_URL + "/v3/stream/" + streamName + "/status/true", streamName);
			log("info notifying stream start stream:" + streamName + " code:" + resCode);
		}

		public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
			log("info unpublishing stream:" + streamName);

			// IMPORTANT. when a streamer successfully connects, but fails to
			// stream because they change their stream name, onpublish will shut
			// down their connection. that's good. BUT, it'll also trigger
			// onunpublish, so we're now sending an end stream event to vdgami,
			// which isn't what we want. TODO. fix!
			//
			// MAYBE do login on stream create instead of onpublish
			int resCode = Request.notifyStreamEvent(VDGAMI_URL + "/v3/stream/" + streamName + "/status/false", streamName);
			log("info notifying stream end stream:" + streamName + " code:" + resCode);

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

		public void onPause(IMediaStream stream, boolean isPause, double location) {
			log("onPause");
		}
	}
}