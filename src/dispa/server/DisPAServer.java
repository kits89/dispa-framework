package dispa.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import dispa.classifier.Classifier;
import dispa.classifier.seacher.Searcher;
import dispa.classifier.seacher.SimpleSearcher;
import dispa.classifier.seacher.WebQueryGenerator;
import dispa.classifier.seacher.WordNetExtensionSearcher;
import dispa.context.VirtualIdentity;
import dispa.context.VirtualIdentityDetector;
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
	//private final static int ERR = 2;
	private final static int OFF = 3;
	private final static String taxonomyFileName = "taxonomy.ser";
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
	public static void main(String[] args) {
		// Parse the command line arguments
		try {
			// Create Options object
			Options options = new Options();

			// Add port option
			options.addOption("p", true, "port where the plug-in must connect to.");
			options.addOption("d", true, "path of the index directory.");
			//options.addOption("n", true, "Enable Named Entity recognition.");
			options.addOption("w", true, "WordNet classification.");
			
			// Parse the command line arguments
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			
			// Get port
			if(cmd.hasOption("p")) {
				PORT = Integer.parseInt(cmd.getOptionValue("p"));
			} else {
				throw new ParseException("The parameter \'p\' is necessary.");
			}

			// Get path of index
			if(cmd.hasOption("d")) {
				INDEX_DIR = cmd.getOptionValue("d");
				if (new File(INDEX_DIR).isDirectory()) {
					throw new Exception("The path " + INDEX_DIR + " is not a directory.");
				}
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
			
			// Initialize id detector
			VirtualIdentityDetector vid = new VirtualIdentityDetector(
					new Classifier(searcher, taxonomy));
			
			// Set up the plugin connection
			pluginConnection = new PluginConnection(PORT);

			try {			
				while(true) {
					System.out.println("Ready. Listening...");
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
						VirtualIdentity virtualIdentity = null;
						switch(opCode) {
						
							// If contents are a query
							case QRY:
								virtualIdentity = vid.getVirtualIdentityId(contents);															
								break;
								
							// If contents are a web resource
							case VST:
								System.out.println(webClassifier.classify(contents));
								break;
								
							case OFF:
								taxonomy.save(taxonomyFileName);
								break;
								
							default:
								System.err.println("The opcode is not valid, a message of error must be sent.");
						}
						pluginConnection.send(Integer.toString(virtualIdentity.getId()));
					}		
				}
			} catch(Exception e) {
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
