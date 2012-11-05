package dispa.bypass.virtualidentities;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.HttpContext;

import dispa.bypass.virtualidentities.cookies.DoubleclickCookieGenerator;
import dispa.bypass.virtualidentities.cookies.GoogleCookieGenerator;



public class VirtualIdentityGenerator {
	DoubleclickCookieGenerator dccGen = new DoubleclickCookieGenerator();
	GoogleCookieGenerator gocGen = new GoogleCookieGenerator();
	
	public HttpContext generateVirtualIdentity(HttpContext context) {
		//TODO: store cookies in the context
		//TODO: store user-agent
		//TODO: store IP?
		CookieStore cookieStore = this.generateVirtualIdentity(
				new BasicCookieStore());
		
		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		return context;
	}
	
	private CookieStore generateVirtualIdentity(CookieStore cookieStore) {
		return dccGen.generateCookie(gocGen.generateCookie(cookieStore));		
	}
}
