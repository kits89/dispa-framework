package dispa.contexts;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import dispa.classifier.Classifier;
import dispa.classifier.NEDetector;

public class ContextManager {
	VirtualIdentityGenerator vig = new VirtualIdentityGenerator();
	
	/**
	 * @uml.property  name="classifier"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Classifier classifier = null;	
	
	/**
	 * @uml.property  name="neDetector"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	 private NEDetector neDetector = new dispa.classifier.NEDetector();

	 /** 
	  * Getter of the property <tt>nEDetector</tt>
	  * @return  Returns the neDetector.
	  */
	 public NEDetector getNEDetector() {
		 return neDetector;
	 }

	 /** 
	  * Setter of the property <tt>nEDetector</tt>
	  * @param nEDetector  The neDetector to set.
	  */
	 public void setNEDetector(NEDetector neDetector) {
		 this.neDetector = neDetector;
	 }
	 
	 public ContextManager(Classifier newClassifier) {
		 classifier = newClassifier;
	 }
	 
	
	public Context generateContext() {
		HttpContext connContext = vig.generateVirtualIdentity(
				new BasicHttpContext());
		Context newContext = new Context(connContext);
		return newContext;
	}
	
	public int getId(String query) {
		// Pool of two threads
		ExecutorService service = Executors.newFixedThreadPool(2);
		
		// Set queries
		neDetector.setQuery(query);
		classifier.setQuery(query);
		
		// Submit threads to the pool
		Future<String> detectIdentifiers = service.submit(neDetector);
		Future<String> detectCategory = service.submit(classifier);
		
		// Calls
		String identifiers = null, categoryPath = null;
		try {
			identifiers = detectIdentifiers.get();
			categoryPath = detectCategory.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		int id = (identifiers + categoryPath).hashCode();		

		// Shutdown thread pool
		service.shutdown();
		
		return id;		
	}
}
