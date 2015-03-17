package tv.videogami.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.util.JSONPObject;

public class Request {

	public static int notifyStreamEvent(String url, String streamName) {
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

	public static int streamerLogin(String url, String streamname, String password) {
		StringBuffer response = null;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			String charset = "UTF-8";
			String query = String.format("stream=%s&password=%s",					
					URLEncoder.encode(streamname, charset),
					URLEncoder.encode(password, charset));
			wr.writeBytes(query);
			wr.flush();
			wr.close();

//			TODO using resCode as a simple yes or no cause can't parse JSON in java yet. fix!
			int resCode = con.getResponseCode();
			return resCode;

//			InputStream is = con.getInputStream();
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//			response = new StringBuffer();
//			String line;
//			while ((line = rd.readLine()) != null) {
//				response.append(line);
//				// response.append('\r'); // appending \r messes up stdout
//			}
//			rd.close();
//			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}
}
