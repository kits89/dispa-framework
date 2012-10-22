package dispa.classifier.seacher;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.lucene.wordnet.SynLookup;

public class WordNetExtensionSearcher extends Searcher {

	public WordNetExtensionSearcher(String indexDir) {
		super(indexDir);
	}
	public WordNetExtensionSearcher(String indexDir, QueryGenerator newQueryGenerator) {
		super(indexDir, newQueryGenerator);
	}

	@Override
	protected Query extendQuery(String strQuery) {
		Query query = null;
		try {
			query = SynLookup.expand(strQuery, 
					wordNetSearcher, analyzer, "Contents", (float) 0.9);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return query;	
	}
}
