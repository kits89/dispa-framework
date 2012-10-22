package dispa.classifier.seacher;

/**
 * The QueryGenerator class is used to build a query from a resource.
 * <p>
 * A resource is any kind of text. For example, a document, a web site, etc.  
 *  QueryGenerator provides methods to extract keywords and build a String that 
 *  represents it. 
 * 
 * @author mjuarez@iiia.csic.es
 * @see SimpleQueryGenerator
 * @see WebQueryGenerator
 *
 */
public abstract class QueryGenerator {
	/**
	 * This method extracts keywords and builds a query in the form of a String.
	 * 
	 * @param resource
	 * 	The raw text from where keywords will be extracted.
	 * @return 
	 * 	Return a String that represents the query.
	 */
	protected abstract String generateQuery(String resource);
}