package tv.videogami.wowza;

import com.wowza.wms.authentication.*;
import com.wowza.wms.client.IClient;
import com.wowza.wms.logging.WMSLoggerFactory;
import java.sql.*;

public class AuthProvider extends AuthenticateUsernamePasswordProviderBase {

	// TODO put in Utils. extend ModuleBase to get getLogger
	private void log(String msg) {
		WMSLoggerFactory.getLogger(null).info("WASA " + msg);
	}

	public String getPassword(String username) {
		String password = null;
		
//		TODO. query vdgami api for stream name, username and password combo
		password = username;
		log("INFO AUTH getting password username:" + username + " password:" + password);
		
		IClient client = this.getClient();
		client.getProperties().setProperty("username", username);
		client.getProperties().setProperty("password", password); // don't really need to send password to streamevents.onpublish
				
		return password;

		// TODO. use JDBC SQL or query vdgami db directly
		// Connection conn = null;
		// try
		// {
		// conn =
		// DriverManager.getConnection("jdbc:mysql://localhost/wowza?user=root&password=mypassword");
		//
		// Statement stmt = null;
		// ResultSet rs = null;
		//
		// try
		// {
		// stmt = conn.createStatement();
		// rs =
		// stmt.executeQuery("SELECT pwd FROM users where username = '"+username+"'");
		// while (rs.next())
		// {
		// pwd = rs.getString("pwd");
		// }
		//
		// }
		// catch (SQLException sqlEx)
		// {
		// WMSLoggerFactory.getLogger(null).error("sqlexecuteException: " +
		// sqlEx.toString());
		// }
		// finally
		// {
		// if (rs != null)
		// {
		// try
		// {
		// rs.close();
		// }
		// catch (SQLException sqlEx)
		// {
		//
		// rs = null;
		// }
		// }
		//
		// if (stmt != null)
		// {
		// try
		// {
		// stmt.close();
		// }
		// catch (SQLException sqlEx)
		// {
		// stmt = null;
		// }
		// }
		// }
		//
		// conn.close();
		// }
		// catch (SQLException ex)
		// {
		// // handle any errors
		// System.out.println("SQLException: " + ex.getMessage());
		// System.out.println("SQLState: " + ex.getSQLState());
		// System.out.println("VendorError: " + ex.getErrorCode());
		// }
		//
		// return pwd;
	}

	public boolean userExists(String username) {
		// return true is user exists
		return false;
	}
}