package dispa.bypass.contexts;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.http.protocol.HttpContext;

public class Context implements Externalizable {

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
	
	/**
	 * Default constructor needed to deserialize Context
	 */
	public Context() {
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

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		id = in.readInt();
		//connectionContext.setAttribute("cookie-store", in.readObject());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(id);
		//out.writeObject(connectionContext.getAttribute("cookie-store"));		
	}
}
