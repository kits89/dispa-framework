package dispa.contexts;


public class VirtualIdentity {

	/**
	 * @uml.property  name="id"
	 */
	private int id;


	public VirtualIdentity(int newId) {
		this.setId(newId);
	}
	
	/**
	 * Getter of the property <tt>id</tt>
	 * @return   Returns the id.
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter of the property <tt>id</tt>
	 * @param id   The id to set.
	 * @uml.property  name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}

}
