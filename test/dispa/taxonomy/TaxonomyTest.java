package dispa.taxonomy;

import org.junit.Assert;

import junit.framework.TestCase;

public class TaxonomyTest extends TestCase {
	/**
	 * @uml.property  name="rootName"
	 */
	String rootName = "Top";

	public void testAddQuery() {
		Taxonomy t = new Taxonomy(rootName);
		
		Category society = new Category("Society");
		Category advice = new Category("Advice");
		
		t.addElement(society, "Top");		
		t.addElement(advice, "Top/Society");
		
		String[] parents = t.getParents("Top/Society/Advice");
		long[] queries = new long[parents.length];
		
		for (int i = 0; i < parents.length; ++i) {
			queries[i] = t.findCategory(parents[i]).getAssignedQueries()+1;			
		}
		
		t.addQuery("Top/Society/Advice");
		
		long[] resultQueries = new long[parents.length];
		for (int i = 0; i < parents.length; ++i) {
			resultQueries[i] = t.findCategory(parents[i]).getAssignedQueries();			
		}
		
		Assert.assertArrayEquals(queries, resultQueries);
	}
	
	public void testGetParents() {
		Taxonomy t = new Taxonomy(rootName);
		
		Category society = new Category("Society");
		Category advice = new Category("Advice");
		
		t.addElement(society, "Top");		
		t.addElement(advice, "Top/Society");
		
		String[] parents = {"Top/Society", "Top"};
		String[] resultParents = t.getParents("Top/Society/Advice");
		
		Assert.assertArrayEquals(parents, resultParents);
		
	}
	
	public void testFindCategory() {
		Taxonomy t = new Taxonomy(rootName);
		
		t.addElement(new Category("Society"), "Top");
		
		t.addElement(new Category("Advice"), "Top/Society");
		
		assertTrue(t.findCategory("Top/Society/Advice").getName().equals("Advice"));
		
		assertTrue(t.findCategory("Top/Society/Foo") == null);
	}

	public void testAddElement() {
		Taxonomy t = new Taxonomy(rootName);
		
		t.addElement(new Category("Society"), "Top");
		
		t.addElement(new Category("Advice"), "Top/Society");
	}

	public void testLoadFromIndex() {
		String indexPath = "/home/marcjuarez/Documents/ARES/ODPIndex/0.1";
		
		Taxonomy t = new Taxonomy(rootName);
		
		t.loadFromIndex(indexPath);
	}
	
	public void testExists() {
		Taxonomy t = new Taxonomy(rootName);
		
		t.addElement(new Category("Society"), "Top");
		
		t.addElement(new Category("Advice"), "Top/Society");
		
		assertTrue(t.exists("Top/Society"));
		
		assertFalse(t.exists("Top/Society/Foo"));
	}
	
	public void testSave() {
		Taxonomy t = new Taxonomy(rootName), t2 = new Taxonomy(rootName);
		
		t.addElement(new Category("Society"), "Top");
		
		t.addElement(new Category("Advice"), "Top/Society");
		
		t.save("taxonomy.ser");
		
		t2.load("taxonomy.ser");
		
		assertTrue(t2.exists("Top/Society"));
		
		assertTrue(t2.exists("Top/Society/Advice"));
	}

}
