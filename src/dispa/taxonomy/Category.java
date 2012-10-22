package dispa.taxonomy;

import java.util.ArrayList;

public class Category extends TaxonomyElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = null; 	
	/**
	 */
	private long assignedVisits;
	/**
	 */
	private long assignedQueries;
	
	/**
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
	 * @return  Returns the queries.
	 */
	public long getAssignedQueries() {
		return assignedQueries;
	}

	/** 
	 * Setter of the property <tt>queries</tt>
	 * @param queries  The queries to set.
	 */
	public void setAssignedQueries(long assignedQueries) {
		this.assignedQueries = assignedQueries;
	}

	/** 
	 * Getter of the property <tt>webs</tt>
	 * @return  Returns the webs.
	 */
	public long getAssignedVisits() {
		return assignedVisits;
	}

	/** 
	 * Setter of the property <tt>webs</tt>
	 * @param webs  The webs to set.
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
