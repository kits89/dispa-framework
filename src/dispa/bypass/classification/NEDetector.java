package dispa.bypass.classification;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class NEDetector implements Callable<String>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property  name="query"
	 */
	private String query = null;
	
	/**
	 * @uml.property  name="props"
	 * @uml.associationEnd  qualifier="constant:java.lang.String java.lang.String"
	 */
	Properties props = new Properties();
	/**
	 * @uml.property  name="pipeline"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	StanfordCoreNLP pipeline = null;
	/**
	 * @uml.property  name="names"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	List<String> names = null;
	
	final static String localIdsFileName = "local.ids";
	
	/**
	 * Initializes the annotators that will be run on the query
	 */
	public NEDetector() {
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	    pipeline = new StanfordCoreNLP(props);
	    names = new ArrayList<String>();
	    
	    // Reads a file that contains important identifiers of this user
	    if ((new File(localIdsFileName)).exists()) {
			try {
				FileInputStream fstream = new FileInputStream(localIdsFileName);
				DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));	    
			    String strLine;	    
			    while ((strLine = br.readLine()) != null) names.add(strLine);
			    in.close();
			} catch (FileNotFoundException e) {
				File localIdsFile = new File(localIdsFileName);
				try {
					localIdsFile.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}	    	   
	    }
	}
	
	public String detectIdentifiers(String text) {
		ArrayList<String> identifiers = new ArrayList<String>();
		
		// Create an empty Annotation with the given text of the query
		Annotation document = new Annotation(text);

		// Run all Annotators on this text
		pipeline.annotate(document);

		// Detect all sentences in the query
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);
				if (ne.equals("PERSON") || ne.equals("LOCATION") 
						|| (names.indexOf(word.toLowerCase()) != -1)) identifiers.add(word);
			}
		}
		System.out.println("Identifiers detected: " + identifiers.toString());
		return identifiers.toString();
	}

	/**
	 * @param query
	 * @uml.property  name="query"
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return
	 * @uml.property  name="query"
	 */
	public String getQuery() {
		return query;
	}

	@Override
	public String call() throws Exception {		
		return this.detectIdentifiers(this.getQuery());
	}
}
