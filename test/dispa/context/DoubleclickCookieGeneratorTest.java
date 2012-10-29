package dispa.context;

import static org.junit.Assert.*;

import java.net.HttpCookie;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DoubleclickCookieGeneratorTest {
	
	@Test
	public void testGetConnection() {
		DoubleclickCookieGenerator dc = new DoubleclickCookieGenerator();
		
		dc.getConnection();
	}
	
	@Test
	public void testGenerateCookie() {
		DoubleclickCookieGenerator dc = new DoubleclickCookieGenerator();
		
//		List<HttpCookie> cookies1 = gc.getConnection1();
//		
//		List<HttpCookie> cookies2 = gc.getConnection1();
//		
		// Generates different cookies
//		Assert.assertNotSame(cookies1.toArray(), cookies2.toArray());
	}

}
