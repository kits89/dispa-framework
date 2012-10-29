package dispa.taxonomy;

import java.util.ArrayList;

public class Category extends TaxonomyElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = null; 	
	/**
	 * @uml.property  name="assignedVisits"
	 */
	private long assignedVisits;
	/**
	 * @uml.property  name="assignedQueries"
	 */
	private long assignedQueries;
	
	/**
	 * @uml.property  name="children"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="dispa.taxonomy.TaxonomyElement"
	 */
	private ArrayList<TaxonomyElement> children = new ArrayList<TaxonomyElement>();
	
	public void removeElement(TaxonomyElement e) {
		children.remove(e);
	}
	
	public void addElement(TaxonomyElement e) {
		children.add(e);
	}


	public void addVisit() {
		this.assignedVisits++;
	}
	
	public void addQuery() {
		this.assignedQueries++;
	}
	
	/**
	 * Getter of the property <tt>queries</tt>
	 * @return   Returns the queries.
	 * @uml.property  name="assignedQueries"
	 */
	public long getAssignedQueries() {
		return assignedQueries;
	}

	/**
	 * Setter of the property <tt>queries</tt>
	 * @param queries   The queries to set.
	 * @uml.property  name="assignedQueries"
	 */
	public void setAssignedQueries(long assignedQueries) {
		this.assignedQueries = assignedQueries;
	}

	/**
	 * Getter of the property <tt>webs</tt>
	 * @return   Returns the webs.
	 * @uml.property  name="assignedVisits"
	 */
	public long getAssignedVisits() {
		return assignedVisits;
	}

	/**
	 * Setter of the property <tt>webs</tt>
	 * @param webs   The webs to set.
	 * @uml.property  name="assignedVisits"
	 */
	public void setAssignedVisits(long assignedVisits) {
		this.assignedVisits = assignedVisits;
	}
	
	public Category(String newName) {
		setName(newName);		
		this.setAssignedQueries(0);
		this.setAssignedQueries(0);
	}
	

	public void setChildren(ArrayList<TaxonomyElement> children) {
		this.children = children;
	}

	public ArrayList<TaxonomyElement> getChildren() {
		return children;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
}
