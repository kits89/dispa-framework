package dispa.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dispa.bypass.classification.Classifier;
import dispa.bypass.classification.search.Searcher;
import dispa.bypass.classification.search.SimpleSearcher;
import dispa.bypass.classification.search.WebQueryGenerator;
import dispa.bypass.classification.search.WordNetExtensionSearcher;
import dispa.bypass.contexts.Context;
import dispa.bypass.contexts.ContextManager;
import dispa.bypass.queries.Query;
import dispa.bypass.queries.ResultsFetcher;
import dispa.taxonomy.Taxonomy;


public class DisPAServer {
	/** Port where the plugin must connect to */
	private static int PORT = 0;
	/** Path where the index is placed */
	private static String INDEX_DIR = "";

	/** Constant for query messages */
	private final static int QRY = 0;
	/** Constant for web messages */
	private final static int VST = 1;
	private final static int ERR = 2;
	private final static int OFF = 3;
	private final static int RES = 4;
	private final static String taxonomyFileName = "taxonomy.ser";
	private final static String contextsFileName = "contexts.ser";
	
	/**
	 * Class to handle the connection with the plug-in
	 * @uml.property  name="pluginConnection"
	 * @uml.associationEnd  
	 */
	private static PluginConnection pluginConnection = null;

	/**
	 * @param args
	 * 		The arguments are: the path of the directory where the index is placed and
	 * the port to where the plug-in must connect.  
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		// Parse the command line arguments
		try {
			// Create Options object
			Options options = new Options();

			// Create options;
			Option portOption = OptionBuilder.withArgName("port")
					.hasArg()
					.withDescription("port where the plug-in must connect to.")
					.create("p");
			Option dirOption = OptionBuilder.withArgName("index directory")
					.hasArg()
					.withDescription("path of the index directory.")
					.create("d");
			Option nerOption = OptionBuilder
					.withDescription("Enable Named Entity recognition.")
					.create("n");
			Option wordnetOption = OptionBuilder
					.withDescription("enable WordNet classification.")
					.create("w");
			Option helpOption = OptionBuilder
					.withDescription("print this message.")
					.create("h");

			// Add options
			options.addOption(helpOption);
			options.addOption(portOption);
			options.addOption(nerOption);
			options.addOption(dirOption);
			options.addOption(wordnetOption);

			// Parse the command line arguments
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);

			// Create help formatter 
			HelpFormatter formatter = new HelpFormatter();

			// Get path of index
			if(cmd.hasOption("h")) {
				formatter.printHelp("dispa [options]", options );
				System.exit(0);
			}

			// Get port
			if(cmd.hasOption("p")) {
				PORT = Integer.parseInt(cmd.getOptionValue("p"));
			} else {
				throw new ParseException("The parameter \'p\' is necessary.");
			}

			// Get path of index
			if(cmd.hasOption("d")) {
				INDEX_DIR = cmd.getOptionValue("d");				
			} else {
				throw new ParseException("The parameter \'d\' is necessary.");
			}		

			Searcher searcher = null;
			// Acitvate WordNet query extension?
			if(cmd.hasOption("w")) {
				searcher = new WordNetExtensionSearcher(INDEX_DIR);
			} else {
				searcher = new SimpleSearcher(INDEX_DIR);
			}

			// Initialize taxonomy
			Taxonomy taxonomy = new Taxonomy("Top");
			if ((new File(taxonomyFileName)).exists()) {
				taxonomy.load(taxonomyFileName);
			} else {
				taxonomy.loadFromIndex(INDEX_DIR);
			}			

			//Initialize the web classifier
			Classifier webClassifier = new Classifier(
					new SimpleSearcher(INDEX_DIR, new WebQueryGenerator()), taxonomy);

			// Initialize context manager
			ContextManager contextManager = new ContextManager(
					new Classifier(searcher, taxonomy), cmd.hasOption("n"));;
			if ((new File(contextsFileName)).exists()) {
				contextManager.load(contextsFileName);
			}	

			// Set up the plugin connection
			pluginConnection = new PluginConnection(PORT);

			// Results fetcher
			ResultsFetcher resultsFecther = new ResultsFetcher();

			try {			
				while(true) {
					System.out.println("[Dispa Server] - Ready. Listening...");
					// Open requested connection
					pluginConnection.open();

					// Get message from plug-in
					String msg = pluginConnection.getRequest();

					// Parse message
					String[] msgArray = msg.split("\\|");
					int opCode = Integer.parseInt(msgArray[0]);
					String contents = msgArray[1];

					// If message is not empty
					if (!contents.isEmpty()) {
						switch(opCode) {

						// If contents are a query
						case QRY:
							Query q = new Query(contents);
							System.out.println("[DisPA Server] - Query: " + q.getText());
							Context c = null;	
							Elements results = null;
							
							Query q1 = contextManager.queryCache.get(q.getId());
							if (q1 != null) {
								q = q1;
								results = q.getResults();
							} else {				
								int id = contextManager.getContextId(q);
								c = contextManager.contextCache.get(id);
								if (c == null) {
									c = contextManager.generateContext(id);
									contextManager.contextCache.put(id, c);
								}
								contextManager.queryCache.put(q.getId(), q);
								results = resultsFecther.fetch(c, q);
								q.setResults(results);							
							}							

							// Add query
							taxonomy.addQuery(q.getCategory());
							
							// Send results back
							for (Element e : results) {
								pluginConnection.send(RES + "|" + e.outerHtml());
							}							
							break;

							// If contents are a web resource
						case VST:
							System.out.println("[DisPA Server] - Visit: " + contents);
							System.out.println(webClassifier.classify(contents));							
							break;
						case ERR:
							System.out.println("[DisPA Server] - An error ocurred in the plugin.");
						case OFF:
							System.out.println("[DisPA Server] - Taxonomy has been updated.");
							taxonomy.save(taxonomyFileName);
							contextManager.save(contextsFileName);							
							break;

						default:
							System.err.println("The opcode is not valid, a message of error must be sent.");
						}						
					}		
				}
			} catch(Exception e) {
				pluginConnection.send(Integer.toString(ERR));
				System.err.println("[DisPA Server] - An error ocurred: " + e.getMessage());
				e.printStackTrace();
				taxonomy.save(taxonomyFileName);
				contextManager.save(contextsFileName);
				pluginConnection.close();
			}
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed. " + e.getMessage() );
		} catch (FileNotFoundException e) {
			System.err.println("Error: the file was not found.");
		} catch (IOException e) {
			System.err.println("There was an error reading the file.");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} 


	}
}
