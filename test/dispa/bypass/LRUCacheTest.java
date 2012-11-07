package dispa.bypass;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import dispa.bypass.queries.Query;

/**
 * Test for the LRUCache class.
 * <p>
 * This test checks that:
 * <ul>
 * 	<li>When an element is added, the element retrieved is the same.</li>
 *  <li>The cache cannot grow more than 
 *  	{@link dispa.bypass.LRUCache#maxEntries}</li>
 *  <li>If a objects falls out of the cache, <code>get()</code>t returns
 *  <code>null.</code></li>
 * </ul>
 * </p>
 * 
 * @author mjuarez@iiia.csic.es
 * @see dispa.bypass.LRUCache
 */
public class LRUCacheTest {
	/**
	 * A method that tests both, <code>get()</code> and
	 * <code>put()</code> methods.
	 */
	@Test
	public final void testCache() {		
		Map<Integer, Query> cache = new LRUCache<Integer, Query>(3);

		Query q1 = new Query("hola");
		cache.put(1, q1);
		
		assertEquals(q1, cache.get(1));

		cache.put(2, new Query("hola1"));
		cache.put(3, new Query("hola2"));
		cache.put(4, new Query("hola3"));

		assertEquals(cache.size(), 3);
		assertEquals(null, cache.get(1));
	}
}
