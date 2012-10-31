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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import dispa.classifier.Classifier;
import dispa.classifier.seacher.Searcher;
import dispa.classifier.seacher.SimpleSearcher;
import dispa.classifier.seacher.WordNetExtensionSearcher;
import dispa.contexts.ContextManager;
import dispa.contexts.VirtualIdentityGenerator;
import dispa.taxonomy.Taxonomy;


public class FileProcessor {

	private static String FILE_NAME = null;
	private static String INDEX_DIR = null;
	private static String PROF_DIR = null;
	private static String SEPARATOR = "/t";
	private static int NUM_FIELD = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Create Options object
			Options options = new Options();

			// Add port option
			options.addOption("w", false, "WordNet classification.");
			options.addOption("f", true, "name of the file of queries.");						
			options.addOption("d", true, "path of the index directory.");
			options.addOption("p", true, "path of the profile directory.");
			options.addOption("F", true, "field separator.");
			options.addOption("N", true, "number of the query field.");
			options.addOption("t", true, "taxonomy file name.");

			// Parse the command line arguments
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);

			// Get port
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
				// Clean profile directory
				File profileFile = new File(PROF_DIR);				
				if (profileFile != null) {
					if (profileFile.exists()) {
						for (File child : profileFile.listFiles()) child.delete();
						profileFile.delete();
					}
					profileFile.mkdir();
				}
			}
			else {
				throw new ParseException("The parameter \'p\' is necessary.");
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
	
			Taxonomy taxonomy = new Taxonomy("Top");
			// If taxonomy is indicated
			if (cmd.hasOption("t")) {
				taxonomy.load(cmd.getOptionValue("t"));
			} else {
				taxonomy.loadFromIndex(INDEX_DIR);
			}	
			
			Classifier classifier = new Classifier(searcher, taxonomy);
			
			// Build contextManager
			ContextManager contextManager = new ContextManager(classifier);

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
				// Parse the line and get the query
				String[] fields = strLine.split(SEPARATOR);
				String query = fields[NUM_FIELD];

				System.out.println("\nQuery: " + query);

				// Get virtual identity
				int id = contextManager.getId(query);
				
				// Write query to a directory associated to the VirtualIdentity
				write(String.valueOf(id), query);
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
