package dispa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import dispa.bypass.classification.Classifier;
import dispa.bypass.classification.search.Searcher;
import dispa.bypass.classification.search.SimpleSearcher;
import dispa.bypass.classification.search.WordNetExtensionSearcher;
import dispa.bypass.contexts.ContextManager;
import dispa.bypass.queries.Query;
import dispa.taxonomy.Taxonomy;


public class FileProcessor {

	private static String FILE_NAME = null;
	private static String INDEX_DIR = null;
	private static String PROF_DIR = null;
	private static String SEPARATOR = "/t";
	private static int NUM_FIELD = 0;
	
	private final static String taxonomyFileName = "taxonomy.ser";
	
	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			// Create Options object
			Options options = new Options();

			// Create options;
			Option portOption = OptionBuilder.withArgName("profile path")
					.hasArg()
					.withDescription("path of the profile directory.")
					.create("p");
			Option dirOption = OptionBuilder.withArgName("index path")
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
			Option fileOption = OptionBuilder.withArgName("filename")
					.hasArg()
					.withDescription("name of the file of queries.")
					.create("f");
			Option tokenOption = OptionBuilder.withArgName("token")
					.hasArg()
					.withDescription("field separator token.")
					.create("F");
			Option positionOption = OptionBuilder.withArgName("position")
					.hasArg()
					.withDescription("number of the query field position.")
					.create("N");
			
			// Add options
			options.addOption(helpOption);
			options.addOption(portOption);
			options.addOption(nerOption);
			options.addOption(dirOption);
			options.addOption(wordnetOption);
			options.addOption(fileOption);
			options.addOption(tokenOption);
			options.addOption(positionOption);
			
			// Parse the command line arguments
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			
			// Create help formatter 
			HelpFormatter formatter = new HelpFormatter();

			// Print help
			if(cmd.hasOption("h")) {
				formatter.printHelp("dispa-fproc [options]", options );
				System.exit(0);
			}
			
			// Get file
			if(cmd.hasOption("f")) {
				FILE_NAME = cmd.getOptionValue("f");
			}
			else {
				throw new ParseException("The parameter \'f\' is necessary.");
			}

			// Get path of index
			if(cmd.hasOption("d")) {
				INDEX_DIR = cmd.getOptionValue("d");
			}
			else {
				throw new ParseException("The parameter \'d\' is necessary.");
			}

			// Get path of profile
			if(cmd.hasOption("p")) {
				PROF_DIR = cmd.getOptionValue("p");
			}
			else {
				PROF_DIR = "PROF" + FILE_NAME;
			}
			if (cmd.hasOption("n")) {
				PROF_DIR = "NER_" + "PROF" + FILE_NAME;
			}
			
			
			// Clean profile directory
			File profileFile = new File(PROF_DIR);				
			if (profileFile != null) {
				if (profileFile.exists() && profileFile.isDirectory()) {
					for (File child : profileFile.listFiles()) child.delete();
					profileFile.delete();
				}
				profileFile.mkdir();
			}

			// Get separator
			if(cmd.hasOption("F")) {
				SEPARATOR = cmd.getOptionValue("F");
			}

			// Get number of field
			if(cmd.hasOption("N")) {
				NUM_FIELD = Integer.parseInt(cmd.getOptionValue("N"));
			}

			Searcher searcher = null;
			// Enable WordNet query extension?
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
			
			Classifier classifier = new Classifier(searcher, taxonomy);
			
			// Build contextManager
			ContextManager contextManager = new ContextManager(classifier, cmd.hasOption("n"));

			// Read file
			BufferedReader br = null;
			DataInputStream in = null;

			// Open the file
			FileInputStream fstream = new FileInputStream(FILE_NAME);

			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			String strLine = null;
			while ((strLine=br.readLine()) != null) {
				if (!(strLine.isEmpty() || strLine.equalsIgnoreCase("-"))) {
					// Parse the line and get the query
					String[] fields = strLine.split(SEPARATOR);
					String query = fields[NUM_FIELD];
	
					System.out.println("\nQuery: " + query);
	
					// Get virtual identity
					int id = contextManager.getContextId(new Query(query));
					
					// Write query to a directory associated to the VirtualIdentity
					write(String.valueOf(id), query);
				}
			}
			
			// Show profiles generated and number of queries		
			System.out.println("Server-logs generated:");
			for (File child : profileFile.listFiles()){
				System.out.println(child.getName());
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

	private static void write(String fileName, String query) {		
		BufferedWriter out = null;
		try {
			// Create file 
			FileWriter fstream = new FileWriter(PROF_DIR + "/"  + fileName, true);
			out = new BufferedWriter(fstream);
			out.write(query);
			out.newLine();

		} catch (IOException e) {
			System.err.println("Error: the query was not written to disk.");
		} finally {
			//Close the output stream
			try {
				if (out != null) {
					out.close();
				}				
			} catch (IOException e) { }
		}	
	}
}
