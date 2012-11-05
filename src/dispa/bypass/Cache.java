package dispa.bypass;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Cache<T> extends ArrayDeque<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int MAX_SIZE;
	
	public Cache(int maxSize) {
		MAX_SIZE = maxSize;
	}
	
	public T store(T e) {
		if (super.size() >= MAX_SIZE) {					
			this.pollLast();
		}
		this.addFirst(e);
		return e;
	}
	
	public T lookUp(T e) {
		Iterator<T> it = this.iterator();
		while (it.hasNext()) {
			T current = it.next();
			if (current.equals(e)) {
				this.remove(current);
				this.addFirst(current);
				return current;
			}
		}
		return null;
	}
	
}
