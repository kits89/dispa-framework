package dispa.bypass.classification.search;

import static org.junit.Assert.*;

import org.junit.Test;

import dispa.bypass.classification.search.WebQueryGenerator;

public class WebQueryGeneratorTest {

	@Test
	public void testGenerateQuery() {
		WebQueryGenerator webQueryGenerator = new WebQueryGenerator();
		
		// Only description case
		String fbDescription = "¡Bienvenido a Facebook en Español (España)! " +
				"Facebook es una herramienta social que pone en contacto a la" +
				" gente con sus amigos y con otras personas que trabajan, " +
				"estudian y viven en su entorno. La gente usa Facebook para " +
				"estar en contacto con amigos, subir un número ilimitado de " +
				"fotos, compartir enlaces y vídeos, y saber más sobre las " +
				"personas que conoce.";
		String fbQuery = webQueryGenerator.generateQuery("http://www.facebook.com");
		
		assertEquals(fbDescription, fbQuery);
		
		// Keywords case
		String bbcKeywords = "BBC, bbc.co.uk, bbc.com, Search, British " +
				"Broadcasting Corporation, BBC iPlayer, BBCi";
		String bbcQuery = webQueryGenerator.generateQuery("http://www.bbc.co.uk/");		
		
		assertEquals(bbcQuery, bbcKeywords);
		
		// No keywords, no description		
//		String pipeQuery = webQueryGenerator.generateQuery("http://es.wikipedia.org/wiki/Jorge_Lorenzo");		
//			
//		System.out.println(pipeQuery);
		
	}

}
