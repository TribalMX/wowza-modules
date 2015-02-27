package tv.videogami.utils;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Request {

	public static int notifyStreamStart(String url, String streamName){
		int resCode = -1;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			String charset = "UTF-8";
			String query = String.format("stream=%s&blah=%s", 
					URLEncoder.encode(streamName, charset), 
					URLEncoder.encode("blah", charset));
			wr.writeBytes(query);
			wr.flush();
			wr.close();
	 
			resCode = con.getResponseCode();	
		} catch (Exception e) {
			e.printStackTrace();
			resCode = -1;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return resCode;
	}
}
