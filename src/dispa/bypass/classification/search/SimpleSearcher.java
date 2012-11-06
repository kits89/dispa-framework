package dispa.bypass.classification.search;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

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
			query = this.queryParser.parse(strQuery);
		} catch (ParseException e) {
			e.printStackTrace();					
		}
		return query;
	}
}
