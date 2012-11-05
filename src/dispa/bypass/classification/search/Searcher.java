package dispa.bypass.classification.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.search.params.CountFacetRequest;
import org.apache.lucene.facet.search.params.FacetSearchParams;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public abstract class Searcher {

	/**
	 * @uml.property  name="queryGenerator"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private QueryGenerator queryGenerator = null;
	
	/**
	 * The reader of the document index.
	 * @uml.property  name="documentReader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IndexReader documentReader = null;

	/**
	 * The reader of the taxonomy index.
	 * @uml.property  name="taxonomyReader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private TaxonomyReader taxonomyReader = null;

	/**
	 * The reader of the WordNet index.
	 * @uml.property  name="wordNetReader"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IndexReader wordNetReader = null;

	/**
	 * Lucene's analyzer to parse and tokenize the query.
	 * @uml.property  name="analyzer"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected StandardAnalyzer analyzer = null;

	/**
	 * Document searcher.
	 * @uml.property  name="documentSearcher"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IndexSearcher documentSearcher = null;

	/**
	 * WordNet searcher.
	 * @uml.property  name="wordNetSearcher"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected IndexSearcher wordNetSearcher = null;	

	public Searcher(String indexDir) {
		queryGenerator = new SimpleQueryGenerator();
		initialize(indexDir);
	}
	
	public Searcher(String indexDir, QueryGenerator newQueryGenerator) {
		queryGenerator = newQueryGenerator;
		initialize(indexDir);
	}
	
	private void initialize(String indexDir) {
		// Files of index paths
		File documentFile = new File(indexDir + "/Index");
		File taxonomyFile = new File(indexDir + "/Taxonomy");
		File wordNetFile = new File(indexDir + "/Syns");

		/*
		 *  Indicate index directories.
		 *  The taxonomy and wordnet indexes can be loaded in memory.
		 */
		FSDirectory documentsIndex = null;
		RAMDirectory taxonomyIndex = null;
		RAMDirectory wordNetIndex = null;
		try {
			documentsIndex = new NIOFSDirectory(documentFile);
			taxonomyIndex = new RAMDirectory(
					new NIOFSDirectory(taxonomyFile));
			wordNetIndex = new RAMDirectory(
					new NIOFSDirectory(wordNetFile));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		/*
		 * Specify the analyzer for tokenizing text. The same
		 * analyzer should be used for indexing and searching.
		 */
		analyzer = new StandardAnalyzer(Version.LUCENE_36);

		// Open readers for search
		try {
			documentReader = IndexReader.open(documentsIndex);
			taxonomyReader = new DirectoryTaxonomyReader(
					taxonomyIndex);
			wordNetReader = IndexReader.open(wordNetIndex);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Instantiate the searchers
		documentSearcher = new IndexSearcher(documentReader);
		wordNetSearcher = new IndexSearcher(wordNetReader);
	}
	
	/**
	 * @param query
	 * @return 
	 */
	public List<FacetResult> facetedSearch(String resource, ArrayList<String> interests){
		int numTopDocuments = 10, numTopCategories = 10;
		
		// Build query
		Query query = extendQuery(queryGenerator.generateQuery(resource));
					
		// Set the number of top results		
		TopScoreDocCollector tdc = TopScoreDocCollector.create(numTopDocuments, true);
		
		// Set a faceted search
		FacetSearchParams facetSearchParams = new FacetSearchParams();

		// Search at level of the category in interests
		for (String interest : interests) {
			facetSearchParams.addFacetRequest(
					new CountFacetRequest(
							new CategoryPath(interest, '/'), numTopCategories));
		}

		// To collect the number of hits per facet
		FacetsCollector facetsCollector = 
				new FacetsCollector(facetSearchParams, documentReader, taxonomyReader);
		
		List<FacetResult> results = null;
		try {
			// Collect the number of hits per facet
			documentSearcher
					.search(query, MultiCollector.wrap(tdc, facetsCollector));
			
			// Get the results of the search
			results = facetsCollector.getFacetResults();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return results;		
	}
	
	protected abstract Query extendQuery(String strQuery);

	public int getCardinal(String categoryPath) {
		
		return 0;
	}
}
