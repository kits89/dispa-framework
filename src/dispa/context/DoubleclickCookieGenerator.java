package dispa.context;

import org.apache.http.impl.cookie.BasicClientCookie;

public class DoubleclickCookieGenerator extends CookieGenerator {
	@Override
	protected void setInitialCookies() {
		BasicClientCookie idCookie = new BasicClientCookie("id", "A");
		idCookie.setVersion(0);
		idCookie.setDomain(".doubleclick.net");
		idCookie.setPath("/");
		this.cookieStore.addCookie(idCookie);
	}
}

