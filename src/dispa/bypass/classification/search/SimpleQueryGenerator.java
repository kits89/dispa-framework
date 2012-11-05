package dispa.bypass.classification.search;

/**
 * The class SimpleQueryGenerator extends QueryGenerator.
 * 
 * @author mjuarez@iiia.csic.es
 * @see QueryGenerator
 * @see WebQueryGenerator
 *
 */
public class SimpleQueryGenerator extends QueryGenerator {
	/**
	 * This method is a trivial generation of a query.
	 * 
	 * @param strQuery
	 * 	The query text.
	 * @return
	 * 	The query text. 		
	 */
	@Override
	protected String generateQuery(String strQuery) {
		return strQuery;
	}
}