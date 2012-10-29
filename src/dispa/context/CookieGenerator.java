package dispa.context;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

abstract public class CookieGenerator {

	private String url = null;
	
	public List<HttpCookie> generateCookie() {
		URL connection = this.getConnection();		
		return this.getCookies(connection);
	}
	
	public List<HttpCookie> getCookies(URL server) {
		List<HttpCookie> cookies = null;
		try {
			URLConnection con = server.openConnection();
			
			String headerName = null;
			for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; ++i) {
			 	if (headerName.equalsIgnoreCase("Set-Cookie")) {
			 		cookies = HttpCookie.parse(con.getHeaderField(i));
			 		break;
			 	}
			}		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cookies;	
	}
	
	abstract protected URL getConnection();
	
	public void setUrl(String newUrl) {
		this.url = newUrl;
	}

	public String getUrl() {
		return this.url;
	}
}
