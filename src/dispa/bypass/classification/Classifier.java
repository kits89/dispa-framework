package dispa.bypass.classification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;

import dispa.bypass.classification.search.Searcher;
import dispa.taxonomy.Taxonomy;

public class Classifier implements Callable<String> {
	
	/**
	 * @uml.property  name="query"
	 */
	private String query = null;
	
    /**
	 * The taxonomy of the Open Directory Project.
	 * @uml.property  name="taxonomy"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected Taxonomy taxonomy = null;
	
	/**
	 * @uml.property  name="searcher"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Searcher searcher = null;

	public Classifier(Searcher newSearcher, Taxonomy newTaxonomy) {
		searcher = newSearcher;
		taxonomy = newTaxonomy;
	}

	final private static int MIN_FREQ = 1000; 
	
	public String classify(String resource) {
		String maxCategoryPath = "Others";
		double maxValue = 0.0;
		int freq = 0;
		
		// Get hits of the search
		List<FacetResult> result = searcher.facetedSearch(resource, taxonomy.getInterests());
		
		// Compute weights
		HashMap<String, Double> weights = new HashMap<String, Double>();
		for (FacetResult facetResult : result) {
			FacetResultNode resultNode = facetResult.getFacetResultNode();
			if (resultNode.getNumSubResults() > 0) {
				for (FacetResultNode node : resultNode.getSubResults()) {
					String categoryPath = node.getLabel().toString();
					double count = (double) node.getValue();					
					long visits = taxonomy.getAssignedVisits(categoryPath);
					int cardinal = searcher.getCardinal(categoryPath);
					double weight = (count * (1+visits)) / (1+cardinal);
					weights.put(categoryPath, weight);
					freq += count;
				}
			}
		}
		System.out.println("[DisPA Server] - Weights: " + weights.toString());
		
		// Find category with maximum weight
		for (Map.Entry<String, Double> entry : weights.entrySet()) {
			double value = entry.getValue();
			if (value > maxValue) {
				maxValue = value;
				maxCategoryPath = entry.getKey();
			}
		}

		return maxCategoryPath;
	}
	
//	private HashMap<String, Double> computeWeights(List<FacetResult> hits) {
//		HashMap<String, Double> weights = new HashMap<String, Double>();
//		// Compute weights
//		for (FacetResult facetResult : hits) {
//			FacetResultNode resultNode = facetResult.getFacetResultNode();
//			if (resultNode.getNumSubResults() > 0) {
//				for (FacetResultNode node : resultNode.getSubResults()) {
//					String categoryPath = node.getLabel().toString();
//					double count = (double) node.getValue();
//					long visits = taxonomy.getAssignedVisits(categoryPath);
//					int cardinal = searcher.getCardinal(categoryPath);
//					double weight = (count * (1+visits)) / (1+cardinal);
//					weights.put(categoryPath, weight);
//				}
//			}
//		}
//		System.out.println("[DisPA Server] - Weights: " + weights.toString());
//		return weights;
//	}

	/**
	 * @param query
	 * @uml.property  name="query"
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return
	 * @uml.property  name="query"
	 */
	public String getQuery() {
		return query;
	}

	@Override
	public String call() throws Exception {		
		return this.classify(this.getQuery());
	}
}
