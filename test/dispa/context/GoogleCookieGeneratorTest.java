package dispa.context;

import static org.junit.Assert.*;

import java.net.HttpCookie;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class GoogleCookieGeneratorTest {

	@Test
	public void testGetConnectionString() {
		GoogleCookieGenerator gc = new GoogleCookieGenerator();
		
		List<HttpCookie> cookies1 = gc.getConnection1();
		
		List<HttpCookie> cookies2 = gc.getConnection1();
		
		// Generates different cookies
		Assert.assertNotSame(cookies1.toArray(), cookies2.toArray());
	}

}
