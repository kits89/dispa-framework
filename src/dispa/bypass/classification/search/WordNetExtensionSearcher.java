package dispa.bypass.classification.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.wordnet.SynLookup;

public class WordNetExtensionSearcher extends Searcher {

	/**
	 * The reader of the WordNet index.
	 * @uml.property  name="wordNetReader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IndexReader wordNetReader = null;
	/**
	 * WordNet searcher.
	 * @uml.property  name="wordNetSearcher"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IndexSearcher wordNetSearcher = null;
	
	public WordNetExtensionSearcher(String indexDir) {
		super(indexDir);
	}
	public WordNetExtensionSearcher(String indexDir, QueryGenerator newQueryGenerator) {
		super(indexDir, newQueryGenerator);
		File wordNetFile = new File(indexDir + "/Syns");
		RAMDirectory wordNetIndex = null;
		try {
			wordNetIndex = new RAMDirectory(
					new NIOFSDirectory(wordNetFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			wordNetReader = IndexReader.open(wordNetIndex);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		wordNetSearcher = new IndexSearcher(wordNetReader);
	}

	@Override
	protected Query extendQuery(String strQuery) {
		Query query = null;
		try {
			query = SynLookup.expand(strQuery, 
					wordNetSearcher, analyzer, "contents", (float) 0.9);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return query;	
	}
}
