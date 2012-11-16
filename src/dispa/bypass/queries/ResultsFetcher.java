package dispa.bypass.queries;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import dispa.bypass.contexts.Context;

public class ResultsFetcher {
	DefaultHttpClient httpclient = null;
	String numResults = "100";
	String language = "es";

	public ResultsFetcher() {
		httpclient = new DefaultHttpClient();
	}
	public ResultsFetcher(int numResults, String language) {
		httpclient = new DefaultHttpClient();
		this.numResults = Integer.toString(numResults);
		this.language = language;
	}
	public String fetch(Context localContext, Query query) {
		String results = null;
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("www.google.com").setPath("/search")
		    .setParameter("safe", "off")
		    .setParameter("complete", "0")
		    .setParameter("num", this.numResults)
		    .setParameter("hl", this.language)
		    .setParameter("q", query.getText());
		
		try {
			URI uri = builder.build();
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = httpclient.execute(httpget, 
					localContext.getConnextionContext());
			
			HttpEntity entity = response.getEntity();
			
			String resultsHTML = EntityUtils.toString(entity);
			
			Document doc = Jsoup.parse(resultsHTML);
			
			Elements element = doc.select("ol");
			
			results = element.html();
			
			EntityUtils.consume(entity);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return results;
	}
	
}
