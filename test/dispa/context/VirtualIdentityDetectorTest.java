package dispa.context;

import static org.junit.Assert.*;

import org.junit.Test;

import dispa.classifier.Classifier;
import dispa.classifier.seacher.Searcher;
import dispa.classifier.seacher.SimpleSearcher;
import dispa.taxonomy.Taxonomy;

public class VirtualIdentityDetectorTest {
	/**
	 * @uml.property  name="indexPath"
	 */
	String indexPath = "/home/marcjuarez/Documents/ARES/ODPIndex/0.1";
			
	@Test
	public void testVirtualIdentityDetector() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetVirtualIdentityId() {
		Searcher searcher = new SimpleSearcher(indexPath);
		Taxonomy taxonomy = new Taxonomy("Top");
		taxonomy.loadFromIndex(indexPath);
		Classifier classifier = new Classifier(searcher, taxonomy);
		VirtualIdentityDetector vid = new VirtualIdentityDetector(classifier);
		
		VirtualIdentity id = vid.getVirtualIdentityId("football team Barcelona");
		assertEquals(id, ("[]"+"Top/Sports").hashCode());
	}

	@Test
	public void testGetNEDetector() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetNEDetector() {
		fail("Not yet implemented");
	}

}
