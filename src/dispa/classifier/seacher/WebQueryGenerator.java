package dispa.classifier.seacher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class WebQueryGenerator extends QueryGenerator {
	@Override
	protected String generateQuery(String resource) {
		String title = null, url = null;
				
		url = resource;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doc != null) {
			title = doc.title();
			Elements tags = doc.select("meta");
			for (Element tag : tags) {
				if (tag.attr("name").equalsIgnoreCase("keywords")) {
					return tag.attr("content");
				} else 	if (tag.attr("name").equalsIgnoreCase("description")) {
					return tag.attr("content").toString();		
				}
			}
		}
		try {
			return ArticleExtractor.INSTANCE.getText(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		}

		return title;
	}
}
