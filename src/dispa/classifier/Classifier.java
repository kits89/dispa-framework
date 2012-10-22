package dispa.classifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;

import dispa.classifier.seacher.Searcher;
import dispa.taxonomy.Taxonomy;

public class Classifier implements Callable<String> {
	
	private String query = null;
	
    /** 
	 * The taxonomy of the Open Directory Project.
	 */
	protected Taxonomy taxonomy = null;
	
	/**
	 */
	private Searcher searcher = null;

	public Classifier(Searcher newSearcher, Taxonomy newTaxonomy) {
		searcher = newSearcher;
		taxonomy = newTaxonomy;
	}

	public String classify(String resource) {
		String maxCategoryPath = null;
		double maxValue = 0.0;
		
		// Get hits of the search
		List<FacetResult> result = searcher.facetedSearch(resource, taxonomy.getInterests());
		
		// Compute weights
		HashMap<String, Double> weights = this.computeWeights(result);
		
		// Find category with maximum weight
		for (Map.Entry<String, Double> entry : weights.entrySet()) {
			double value = entry.getValue();
			if (value > maxValue) {
				maxValue = value;
				maxCategoryPath = entry.getKey();
			}
		}
		
		// Add query
		taxonomy.addQuery(maxCategoryPath);

		return maxCategoryPath;
	}
	
	private HashMap<String, Double> computeWeights(List<FacetResult> hits) {
		HashMap<String, Double> weights = new HashMap<String, Double>();
		// Compute weights
		for (FacetResult facetResult : hits) {
			FacetResultNode resultNode = facetResult.getFacetResultNode();
			if (resultNode.getNumSubResults() > 0) {
				for (FacetResultNode node : resultNode.getSubResults()) {
					String categoryPath = node.getLabel().toString();
					int count = (int) node.getValue();
					long visits = taxonomy.getAssignedVisits(categoryPath);
					int cardinal = searcher.getCardinal(categoryPath);
					double weight = (count * (1+visits)) / (1+cardinal);
					weights.put(categoryPath, weight);					
				}
			}
		}
		System.out.println("Weights: "+ weights.toString());
		return weights;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	@Override
	public String call() throws Exception {		
		return this.classify(this.getQuery());
	}
}
