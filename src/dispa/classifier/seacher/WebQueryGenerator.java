package dispa.classifier.seacher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class WebQueryGenerator extends QueryGenerator {
	@Override
	protected String generateQuery(String resourceAddress) {
		String title = null, description = null, keywords = null;

		try {
			URL url = new URL(resourceAddress);
			String contents = this.getWebContents(url);

			if (contents != null) {
				Document doc = null;
				try {
					doc = Jsoup.parse(contents);
					title = doc.title();
					Elements tags = doc.select("meta");
					boolean kw = false, ds = false;
					for (Element tag : tags) {
						if (tag.attr("name").equalsIgnoreCase("keywords")) {
							keywords = tag.attr("content");
							kw = true;
						} else 	if (tag.attr("name").equalsIgnoreCase("description")) {
							description = tag.attr("content").toString();
							ds = true;
						}
						if (ds && kw) {
							break;
						}
					}
					if (keywords != null) {
						return keywords;
					} else if (description != null) {
						return description;
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}

				try {
					return ArticleExtractor.INSTANCE.getText(contents);
				} catch (BoilerpipeProcessingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				return title;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return resourceAddress;
	}

	private String getWebContents(URL url) {
		String contents = null;
		URLConnection con;
		try {
			con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			contents = new String(baos.toByteArray(), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contents;
	}
}
