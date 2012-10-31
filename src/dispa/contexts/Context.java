package dispa.contexts;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class Context {

	HttpContext connectionContext = new BasicHttpContext();
	int id = 0;
	
	public Context(HttpContext newConnContext) {
		this.connectionContext = newConnContext;
	}
}
