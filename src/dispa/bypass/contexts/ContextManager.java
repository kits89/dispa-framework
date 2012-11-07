package dispa.bypass.contexts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import dispa.bypass.LRUCache;
import dispa.bypass.classification.Classifier;
import dispa.bypass.classification.NEDetector;
import dispa.bypass.queries.Query;
import dispa.bypass.virtualidentities.VirtualIdentityGenerator;

public class ContextManager {
	public LRUCache<Integer, Query> queryCache = new LRUCache<Integer, Query>(15);
	public LRUCache<Integer, Context> contextCache = new LRUCache<Integer, Context>(40);

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
	private NEDetector neDetector = null;

	public ContextManager(Classifier newClassifier, boolean ner) {
		classifier = newClassifier;
		if (ner) {
			neDetector = new dispa.bypass.classification.NEDetector();
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

		int id = (identifiers + categoryPath).hashCode();		

		// Shutdown thread pool
		service.shutdown();

		return id;		
	}

	@SuppressWarnings("unchecked")
	public void load(String contextsFileName) {
		try {
			FileInputStream fileIn =
					new FileInputStream(contextsFileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			contextCache = (LRUCache<Integer, Context>) in.readObject();
			queryCache = (LRUCache<Integer, Query>) in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save(String contextsFileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(contextsFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(contextCache);
			out.writeObject(queryCache);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
