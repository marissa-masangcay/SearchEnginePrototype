import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This software driver class provides a consistent entry point for the search
 * engine. Based on the arguments provided to {@link #main(String[])}, it
 * creates the necessary objects and calls the necessary methods to build an
 * inverted index, process search queries, configure multithreading, and launch
 * a web server (if appropriate).
 */
public class Driver {

    /**
     * Flag used to indicate the following value is an input directory of text
     * files to use when building the inverted index.
     * 
     * @see "Projects 1 to 5"
     */
    public static final String INPUT_FLAG = "-input";

    /**
     * Flag used to indicate the following value is the path to use when
     * outputting the inverted index to a JSON file. If no value is provided,
     * then {@link #INDEX_DEFAULT} should be used. If this flag is not provided,
     * then the inverted index should not be output to a file.
     * 
     * @see "Projects 1 to 5"
     */
    public static final String INDEX_FLAG = "-index";

    /**
     * Flag used to indicate the following value is a text file of search
     * queries.
     * 
     * @see "Projects 2 to 5"
     */
    public static final String QUERIES_FLAG = "-query";

    /**
     * Flag used to indicate the following value is the path to use when
     * outputting the search results to a JSON file. If no value is provided,
     * then {@link #RESULTS_DEFAULT} should be used. If this flag is not
     * provided, then the search results should not be output to a file.
     * 
     * @see "Projects 2 to 5"
     */
    public static final String RESULTS_FLAG = "-results";

    /**
     * Flag used to indicate the following value is the number of threads to use
     * when configuring multithreading. If no value is provided, then
     * {@link #THREAD_DEFAULT} should be used. If this flag is not provided,
     * then multithreading should NOT be used.
     * 
     * @see "Projects 3 to 5"
     */
    public static final String THREAD_FLAG = "-threads";

    /**
     * Flag used to indicate the following value is the seed URL to use when
     * building the inverted index.
     * 
     * @see "Projects 4 to 5"
     */
    public static final String SEED_FLAG = "-seed";

    /**
     * Flag used to indicate the following value is the port number to use when
     * starting a web server. If no value is provided, then
     * {@link #PORT_DEFAULT} should be used. If this flag is not provided, then
     * a web server should not be started.
     */
    public static final String PORT_FLAG = "-port";

    /**
     * Default to use when the value for the {@link #INDEX_FLAG} is missing.
     */
    public static final String INDEX_DEFAULT = "index.json";

    /**
     * Default to use when the value for the {@link #RESULTS_FLAG} is missing.
     */
    public static final String RESULTS_DEFAULT = "results.json";

    /**
     * Default to use when the value for the {@link #THREAD_FLAG} is missing.
     */
    public static final int THREAD_DEFAULT = 5;
    
    /**
     * Default to use when the value for the {@link #PORT_FLAG} is missing.
     */
    public static final int PORT_DEFAULT = 8080;

    /**
     * Parses the provided arguments and, if appropriate, will build an inverted
     * index from a directory or seed URL, process search queries, configure
     * multithreading, and launch a web server.
     * 
     * @param args
     *            set of flag and value pairs
     */
    public static void main(String[] args) {
        
        ArgumentParser parser = new ArgumentParser(args);
        DirectoryTraversal dir = new DirectoryTraversal();
        FileParser fileParser = new FileParser();
        
        boolean hasIndex = false;
        boolean hasDirectory = false;
        
        
        String directory = null;
        File directoryPath = null;
        File outputFile = null;
        
        
        

        try{
         
            //input = directory to traverse through
            //if args has input 
            if(parser.hasFlag(INPUT_FLAG))
            {
            	if(parser.hasValue(INPUT_FLAG))
            	{
            		directory = parser.getValue(INPUT_FLAG);
            		directoryPath = new File(directory);
            		hasDirectory = true;
            		if(!directoryPath.isDirectory())
            		{
            			hasDirectory = false;
            			System.err.println("Invalid directory");
            		}
            	}
            	else
            	{
            		System.err.println("No directory input, please enter directory");
            	}
            	
            }
            else if(!parser.hasFlag(INPUT_FLAG))
            {
            	System.err.println("No directory found, please enter a directory");
            }
            
            //index = file name to print to
            if(parser.hasFlag(INDEX_FLAG))
            {
            	hasIndex = true;
            	if(parser.getValue(INDEX_FLAG)!=null)
            	{
            		File inputFile = new File(parser.getValue(INDEX_FLAG));
                	if(!inputFile.isFile())
                	{
                		System.err.println("Invalid file");
                	}
                	outputFile = inputFile;
            	}
            	else
            	{
            		outputFile = new File(INDEX_DEFAULT);
            		
            	}
            }

            
            if(hasDirectory = true)
            {
            	dir.traverseDir(directoryPath);
            }

            
            if(hasIndex == true)
            {
            	fileParser.printIndex(outputFile.toString());
            }
        }catch(NullPointerException e){
        	System.err.println("Null pointer exception");
        }
        	
    }
}
