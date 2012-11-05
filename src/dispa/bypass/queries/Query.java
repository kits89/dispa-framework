package dispa.bypass.queries;

import java.io.Serializable;

public class Query implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String text = null;
	String results = null;
	String category = null;
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Query(String newText) {
		this.text = newText;
	}
	
	public void setResults(String newResults) {
		this.results = newResults;
	}
	public String getResults() {
		return results;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equalsIgnoreCase(other.text))
			return false;
		return true;
	}

	public String getText() {
		return this.text;
	}
}
