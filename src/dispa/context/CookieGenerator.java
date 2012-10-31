package dispa.context;

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

	public void generateCookie(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		this.setInitialCookies();		
		httpclient.setCookieStore(this.cookieStore);				
		HttpGet httpget = new HttpGet(url);

		try {
			httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	abstract protected void setInitialCookies();
}
