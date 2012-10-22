package dispa.taxonomy;

public class WebSite extends TaxonomyElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 */
	private String uri = null;
	
	public WebSite(String newUri) {
		this.uri = newUri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

}
