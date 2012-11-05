package dispa.bypass.virtualidentities.cookies;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.cookie.BasicClientCookie;

public class GoogleCookieGenerator extends CookieGenerator {
	final static String URL = "http://www.google.com/";
	@Override
	protected HttpGet setInitialCookies() {
		BasicClientCookie idCookie = new BasicClientCookie("id", "A");
		idCookie.setVersion(0);
		idCookie.setDomain(".doubleclick.net");
		idCookie.setPath("/");
		this.cookieStore.addCookie(idCookie);
		return new HttpGet(URL);
	}
}
