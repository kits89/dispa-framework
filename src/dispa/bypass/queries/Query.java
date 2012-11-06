package dispa.bypass.queries;

import java.io.Serializable;

import org.jsoup.select.Elements;

public class Query implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id = 0;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	String text = null;
	Elements results = null;
	String category = null;
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Query(String newText) {
		this.text = newText;
		this.id = newText.toLowerCase().hashCode();
	}
	
	public void setResults(Elements newResults) {
		this.results = newResults;
	}
	public Elements getResults() {
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
