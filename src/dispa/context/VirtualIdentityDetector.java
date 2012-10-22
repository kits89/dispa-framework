package dispa.context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dispa.classifier.Classifier;
import dispa.classifier.NEDetector;


public class VirtualIdentityDetector {

	/**
	 */
	private Classifier classifier = null;	

	public VirtualIdentityDetector(Classifier newClassifier) {
		classifier = newClassifier;
	}

	public VirtualIdentity getVirtualIdentityId(String query) {
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
		
		return (new VirtualIdentity(id));		
	}

	/**
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


}
