package dispa.context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DoubleclickCookieGenerator extends CookieGenerator {

	private final static String doubleClickURL = "http://www.google.com/settings/ads/onweb";

	@Override
	protected URL getConnection() {
		URL connection = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// Create a local instance of cookie store
			CookieStore cookieStore = new BasicCookieStore();
			// Bind custom cookie store to the local context
			((AbstractHttpClient) httpclient).setCookieStore(cookieStore);
			CookieSpecFactory csf = new CookieSpecFactory() {
				public CookieSpec newInstance(HttpParams params) {
					return new BrowserCompatSpec() {
						@Override
						public void validate(Cookie cookie, CookieOrigin origin)
								throws MalformedCookieException {
							// Allow all cookies
							System.out.println("Allowed cookie: " + cookie.getName() + " "
									+ cookie.getValue() + " " + cookie.getPath());
						}
					};
				}
			};
			((AbstractHttpClient) httpclient).getCookieSpecs().register("EASY", csf);

			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			HttpGet httpget = new HttpGet(doubleClickURL);
			// Override the default policy for this request
			httpclient.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, "EASY"); 
						
			// Pass local context as a parameter
			HttpResponse response = httpclient.execute(httpget, localContext);

			HttpEntity entity = response.getEntity();

			if (entity != null) {				
				for (Cookie  cookie : cookieStore.getCookies()) {
					String cPath = cookie.getPath();
					if (cPath.equals("/settings/ads/onweb/")) {
						URIBuilder builder = new URIBuilder();
						builder.setScheme("https").setHost("www.google.com").setPath(cPath)
						.setParameter("sig", cookie.getValue())
						.setParameter("hl", "en");
						URI uri = builder.build();
						httpget.abort();
						httpget = new HttpGet(uri);
						
						HttpResponse response2 = httpclient.execute(httpget, localContext);
						HttpEntity entity2 = response2.getEntity();
						if (entity2 != null) {
							InputStream instream = entity.getContent();


							BufferedReader reader = new BufferedReader(
									new InputStreamReader(instream));
							// do something useful with the response
							Document document = Jsoup.parse(reader.readLine());
							Element form = document.select("form").first();			
							String optinURL = form.attr("action");
							connection = new URL(optinURL);
							instream.close();
						}

					}
				}				
			}
			httpclient.getConnectionManager().shutdown();
		} catch(IOException e) {

		} catch(Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

}
