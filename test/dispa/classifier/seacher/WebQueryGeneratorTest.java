package dispa.classifier.seacher;

import static org.junit.Assert.*;

import org.junit.Test;

public class WebQueryGeneratorTest {

	@Test
	public void testGenerateQuery() {
		WebQueryGenerator webQueryGenerator = new WebQueryGenerator();
		
		String query = "example query for test";
		
		String generatedQuery = webQueryGenerator.generateQuery(query);
		
		assertEquals(query, generatedQuery);
	}

}
