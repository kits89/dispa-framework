package dispa.bypass;

import static org.junit.Assert.*;

import org.junit.Test;

import dispa.bypass.contexts.Context;
import dispa.bypass.queries.Query;

public class CacheTest {

	@Test
	public void testStore() {
		Cache<Query> cache = new Cache<Query>(3);
		Query q = new Query("hola");
		cache.store(q);
		
		Query q2 = new Query("Hola");
		assertTrue(cache.contains(q2));
		
		cache.store(new Query("hola1"));
		cache.store(new Query("hola2"));
		cache.store(new Query("hola3"));
		
		assertEquals(cache.size(), 3);		
	}

	@Test
	public void testFetch() {
		Cache<Context> cache = new Cache<Context>(2);
		
		Context c1 = new Context(1);
		Context c2 = new Context(2);
		
		cache.store(c1);
		cache.store(c2);				

		assertEquals((Context) cache.peekFirst(), (new Context(2)));
		
		Context c = cache.lookUp(c1);
		
		assertTrue(c == c1);		
		assertEquals(cache.size(), 2);
		assertEquals((Context) cache.peekFirst(), (new Context(1)));
		
		assertEquals(null, cache.lookUp(new Context(4)));
	}

}
