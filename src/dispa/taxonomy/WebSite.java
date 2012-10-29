package dispa.taxonomy;

public class WebSite extends TaxonomyElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="uri"
	 */
	private String uri = null;
	
	public WebSite(String newUri) {
		this.uri = newUri;
	}

	/**
	 * @param uri
	 * @uml.property  name="uri"
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return
	 * @uml.property  name="uri"
	 */
	public String getUri() {
		return uri;
	}

}
