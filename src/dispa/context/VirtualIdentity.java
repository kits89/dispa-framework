package dispa.context;


public class VirtualIdentity {

	/**
	 */
	private int id;


	public VirtualIdentity(int newId) {
		this.setId(newId);
	}
	
	/**
	 * Getter of the property <tt>id</tt>
	 * @return  Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter of the property <tt>id</tt>
	 * @param id  The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

}
