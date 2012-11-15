package dispa.bypass.contexts;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

import dispa.bypass.classification.Classifier;
import dispa.bypass.classification.NEDetector;
import dispa.bypass.queries.Query;
import dispa.bypass.virtualidentities.VirtualIdentityGenerator;

public class ContextManager {	
	public JCS queryCache = null, contextCache = null;
	
	VirtualIdentityGenerator vig = new VirtualIdentityGenerator();

	final private int MIN_FREQ = 1000; 
	
	/**
	 * @uml.property  name="classifier"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Classifier classifier = null;	

	/**
	 * @uml.property  name="neDetector"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NEDetector neDetector = null;

	public ContextManager(Classifier newClassifier, boolean ner) {
		classifier = newClassifier;
		if (ner) {
			neDetector = new dispa.bypass.classification.NEDetector();
		}
		// Set cache
		try {
            queryCache = JCS.getInstance("queries");
            contextCache = JCS.getInstance("contexts");
        } catch (CacheException e) {
            System.err.println("Problem initializing caches: " + e.getCause());
        }
		int key = 1;
        Query q = new Query("hola i adeu");
        try {
            // if it isn't null, insert it
            if (q != null) {
                queryCache.put(key, q);
            }
        } catch (CacheException e) {
        	System.err.println("Problem putting query="
               + q.getText() + " in the cache, for key " + key + ": " + e.getCause());
        }
	}

	
	public Context generateContext(int id) {
		HttpContext connContext = vig.generateVirtualIdentity(
				new BasicHttpContext());
		Context newContext = new Context(id, connContext);
		return newContext;
	}

	public int getContextId(Query query) {
		// Pool of two threads
		ExecutorService service = Executors.newFixedThreadPool(2);

		// Set queries
		if (neDetector != null) {
			neDetector.setQuery(query.getText());
		}
		classifier.setQuery(query.getText());

		// Submit threads to the pool
		Future<String> detectIdentifiers = null;
		if (neDetector != null) {
			detectIdentifiers = service.submit(neDetector);
		}
		Future<String> detectCategory = service.submit(classifier);

		// Calls
		String identifiers = null, categoryPath = null;
		try {
			if (neDetector != null) {
				identifiers = detectIdentifiers.get();
			}
			categoryPath = detectCategory.get();
			System.out.println("[DisPA Server] - Category: " + categoryPath);
			query.setCategory(categoryPath);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		String strId = identifiers + categoryPath;
		System.out.println("[DisPA Server] - Frequency: " + classifier.getFrequency());
		if (classifier.getFrequency() < this.MIN_FREQ) {
			if (identifiers == null) {
				strId += query.getText().toLowerCase();
			} else if (identifiers.isEmpty()) {
				strId += query.getText().toLowerCase();
			}
		}

		// Shutdown thread pool
		service.shutdown();

		return strId.hashCode();		
	}
}
