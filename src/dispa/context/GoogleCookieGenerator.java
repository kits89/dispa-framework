package dispa.context;

import java.net.MalformedURLException;
import java.net.URL;

public class GoogleCookieGenerator extends CookieGenerator {

	private final static String googleURL = "http://www.google.com/";
	
	@Override
	protected URL getConnection() {
		URL connection = null;
		try {
			connection = new URL(googleURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
}
