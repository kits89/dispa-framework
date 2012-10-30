package dispa.context;

import static org.junit.Assert.*;

import java.net.HttpCookie;
import java.net.URL;
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
		
		URL cookies1 = dc.getConnection();
		
		URL cookies2 = dc.getConnection();
		
		// Generates different cookies
		//Assert.assertNotSame(cookies1.toArray(), cookies2.toArray());
	}

}
