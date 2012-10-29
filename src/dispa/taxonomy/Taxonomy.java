package dispa.taxonomy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;


public class Taxonomy {

	/**
	 * @uml.property  name="root"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Category root = null;	

	/**
	 * @uml.property  name="interests"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ArrayList<String> interests = new ArrayList<String>();	
	
	public Taxonomy(String rootName) {
		interests.add(rootName);
		root = new Category(rootName);
	}

	/**
	 */
	public void loadFromIndex(String indexPath) {
		try {
			RAMDirectory indexDir = new RAMDirectory(
					new NIOFSDirectory(new File(indexPath + "/Taxonomy")));
			TaxonomyReader tr = new DirectoryTaxonomyReader(indexDir);

			for (int i = TaxonomyReader.ROOT_ORDINAL+2; i < tr.getSize(); ++i) {
				String pathAndName = tr.getPath(i).toString();
				int lastBarIndex = pathAndName.lastIndexOf("/");
				String path = pathAndName.substring(0, lastBarIndex),
						name = pathAndName.substring(lastBarIndex+1, pathAndName.length());

				this.addElement(new Category(name), path);
			}						
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public String[] getParents(String path) {
		ArrayList<String> parents = new ArrayList<String>();
		String sep = "/";
		int i = path.lastIndexOf(sep);
		while (i != -1) {
			parents.add(path.substring(0, i));
			i = path.lastIndexOf(sep, i-1);
		}
		return (String[]) parents.toArray(new String[parents.size()]);		
	}
	
	public void addQuery(String path) {
		String sep = "/";
		int i = path.length();
		while (i != -1) {
			this.findCategory(path.substring(0, i)).addQuery();
			i = path.lastIndexOf(sep, i-1);
		}
	}
	
	public void addVisit(String path) {
		String sep = "/";
		int i = path.length();
		while (i != -1) {
			this.findCategory(path.substring(0, i)).addVisit();
			i = path.lastIndexOf(sep, i-1);
		}
	}
	
	public long getAssignedVisits(String path) {
		return findCategory(path).getAssignedVisits();
	}

	public boolean exists(String path) {
		return this.findCategory(path) != null;
	}

	public Category findCategory(String path) {
		String[] pathNames = path.split("/");		
		Category current = root;
		for (int i = 1; i < pathNames.length; ++i) {
			int j = 0;
			boolean childFound = false;
			ArrayList<TaxonomyElement> children = current.getChildren();			
			while (!childFound && j < children.size()) {
				TaxonomyElement child = children.get(j);
				if (child instanceof Category) {
					if (((Category) child).getName().equals(pathNames[i])) {
						current = ((Category) child);
						childFound = true;
					}
				}
				j++;
			}
			if (!childFound) {
				return null;
			}
		}
		return current;		
	}

	public void addElement(TaxonomyElement e, String path) {				
		findCategory(path).addElement(e);
	}

	public void removeElement(TaxonomyElement e, String path) {				
		findCategory(path).removeElement(e);
	}

	public ArrayList<String> getInterests() {
		return interests;
	}

	public void setInterests(ArrayList<String> newInterests) {
		interests = newInterests;
	}	

	public void save(String taxonomyFileName) {
		// Serialize taxonomy
		try {
			// Use buffering
			OutputStream file = new FileOutputStream(taxonomyFileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(root);
				output.writeObject(interests);
			} finally {
				output.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void load(String taxonomyFileName) {
		try {
			// Use buffering
			InputStream file = new FileInputStream(taxonomyFileName);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			try {
				// Deserialize taxonomy
				this.setRoot((Category)input.readObject());
				this.setInterests((ArrayList<String>)input.readObject());	      
			} finally {
				input.close();
			}
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter of the property <tt>root</tt>
	 * @return   Returns the root.
	 * @uml.property  name="root"
	 */
	public Category getRoot() {
		return root;
	}

	/**
	 * Setter of the property <tt>root</tt>
	 * @param root   The root to set.
	 * @uml.property  name="root"
	 */
	public void setRoot(Category root) {
		this.root = root;
	}
}
