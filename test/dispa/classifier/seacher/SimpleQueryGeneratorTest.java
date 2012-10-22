package dispa.classifier.seacher;

import junit.framework.TestCase;

public class SimpleQueryGeneratorTest extends TestCase {

	public void testGenerateQuery() {
		SimpleQueryGenerator simpleQueryGenerator = new SimpleQueryGenerator();
		
		String query = "example query for test";
		
		String generatedQuery = simpleQueryGenerator.generateQuery(query);
		
		assertEquals(query, generatedQuery);
	}

}
