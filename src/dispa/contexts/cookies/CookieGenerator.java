package dispa.contexts.cookies;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author  mjuarez@iiia.csic.es
 */
abstract public class CookieGenerator {

	protected CookieStore cookieStore = new BasicCookieStore();

	public CookieStore generateCookie(CookieStore cookieStore) {
		DefaultHttpClient httpclient = new DefaultHttpClient();		
		httpclient.setCookieStore(cookieStore);			
		HttpGet httpGet = this.setInitialCookies();

		try {
			httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return cookieStore;
	}	
	abstract protected HttpGet setInitialCookies();
}
