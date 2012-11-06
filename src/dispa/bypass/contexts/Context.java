package dispa.bypass.contexts;

import java.io.Serializable;

import org.apache.http.protocol.HttpContext;

public class Context implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpContext connectionContext = null;
	int id = 0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Context(int newId) {
		this.id = newId;
	}
	
	public Context(int newId, HttpContext newConnContext) {
		this.id = newId;
		this.connectionContext = newConnContext;
	}
	
	public HttpContext getConnextionContext() {
		return this.connectionContext;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Context other = (Context) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
