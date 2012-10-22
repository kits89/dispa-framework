package dispa.classifier.seacher;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SimpleSearcher extends Searcher {

	public SimpleSearcher(String indexDir) {
		super(indexDir);
	}
	
	public SimpleSearcher(String indexDir, QueryGenerator newQueryGenerator) {
		super(indexDir, newQueryGenerator);
	}

	@Override
	protected Query extendQuery(String strQuery) {
		// Create the query		
		Query query = null;
		try {
			query = new QueryParser(Version.LUCENE_36, "Contents", analyzer)
				.parse(strQuery);
		} catch (ParseException e) {
			e.printStackTrace();					
		}
		return query;
	}
}
