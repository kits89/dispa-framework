package dispa.bypass;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <code>LRUCache</code> is an implementation of a cache following an LRU
 * policy based on a <code>LinkedHashMap</code>. The cache contains keys
 * with an associated value of a generic type.
 *
 * @author mjuarez@iiia.csic.es
 * @param <K>
 * 		the key of the cache entry.
 * @param <V>
 * 		the value of the entry associated to <K>.
 * @see java.util.LinkedHashMap
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V>
		implements Serializable {
	/** 
	 * File where the elements of the cache are stored
	 */
	private File cacheFile = null;
	
	/**
	 * UID of the class for serialization.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Maximum number of entries in the cache.
	 */
	private final int maxEntries;

	/**
	 * Default constructor of the cache.
	 *
	 * @param newMaxEntries
	 * 		the maximum number of entries in the cache.
	 */
	public LRUCache(final int newMaxEntries, String fileName) {
		super(newMaxEntries + 1, 1.0f, true);
		this.maxEntries = newMaxEntries;
		this.cacheFile = new File(fileName);
	}

	
	@Override
	public V get(Object key) {
		V VObject = super.get(key);
		if (VObject == null) {
			// TODO: Fetch from disk
			
		}
		return VObject;
	}

	@Override
	public V put(K key, V value) {
		// TODO: Write to disk
		
		return super.put(key, value);
	}

	/**
	 * Returns <tt>true</tt> if this <code>LRUCache</code> has more entries
	 * than the maximum specified when it was created.
	 *
	 * <p>
	 * This method <em>does not</em> modify the underlying <code>Map</code>;
	 * it relies on the implementation of <code>LinkedHashMap</code> to do
	 * that, but that behavior is documented in the JavaDoc for
	 * <code>LinkedHashMap</code>.
	 * </p>
	 *
	 * @param eldest
	 * 		the <code>Entry</code> in question; this implementation
	 *      doesn't care what it is, since the implementation is only
	 *      dependent on the size of the cache.
	 * @return <tt>true</tt> if the oldest
	 * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
	 */
	@Override
	protected final boolean
			removeEldestEntry(final Map.Entry<K, V> eldest) {
		return super.size() > maxEntries;
	}
}
