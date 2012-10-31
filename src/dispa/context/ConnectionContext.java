package dispa.context;

public class ConnectionContext extends VirtualIdentity {

	/**
	 * @uml.property  name="cookies"
	 */
	//private ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
	// static because all contexts use the same user-agent
	//private static String userAgent = null;
	
	
	public ConnectionContext(int newId) {
		super(newId);
	}

}
