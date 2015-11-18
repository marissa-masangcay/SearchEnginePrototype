import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        // TODO Try to simplify Driver
        /*
        if (input flag) do stuff
        if (search flag) do stuff
        
        then handle output
        if (index flag) do stuff
        if (result flag) do stuff
        */
        
        ArgumentParser argumentParser = new ArgumentParser(args);
        InvertedIndex invertedIndex = new InvertedIndex();
        QueryParser queryParser = new QueryParser(invertedIndex);
               
        String directoryToTraverse = null;
        Path directory = null;
        Path outputFile = null;
        Path resultsFile = null;
        Path queriesFile = null;
        
        try{        
            //input = directory to traverse through
        	//if args has an input flag
            if(argumentParser.hasFlag(INPUT_FLAG))
            {
            	//if input flag has a value
            	if(argumentParser.hasValue(INPUT_FLAG))
            	{
            		directoryToTraverse = argumentParser.getValue(INPUT_FLAG);
            		directory = Paths.get(directoryToTraverse);
            		
            		//if directory isn't a valid directory
            		if(!Files.isDirectory(directory))
            		{
            			System.err.println("Invalid directory");
            		}
            	}
            	else //if input flag has no value
            	{
            		System.err.println("No directory entered, please enter valid directory");
            	}	
            }
            //if args has no input flag
            else if(!argumentParser.hasFlag(INPUT_FLAG))
            {
            	System.err.println("No directory found, please enter a directory");
            }
            
            //index = file name to print to
            //if args has an index flag
            if(argumentParser.hasFlag(INDEX_FLAG))
            {
            	//if index flag has a value
            	if(argumentParser.getValue(INDEX_FLAG)!=null)
            	{
            		Path userInputFile = Paths.get(argumentParser.getValue(INDEX_FLAG));
            		//if index flag value is not valid
                	if(!userInputFile.isAbsolute())
                	{
                		System.err.println("Invalid file");
                	}
                	outputFile = userInputFile;
            	}
            	//if index flag has no value
            	else
            	{
            		outputFile = Paths.get(INDEX_DEFAULT);
            	}
            }
            
            //query = file name to obtain queries from
            //if args has a query flag
            if (argumentParser.hasFlag(QUERIES_FLAG))
            {
            	if (argumentParser.getValue(QUERIES_FLAG) != null)
            	{
            		queriesFile = Paths.get(argumentParser.getValue(QUERIES_FLAG));
            		//if queries flag is not valid
            		if (!Files.isReadable(queriesFile))
            		{
            			System.err.println("Invalid query file");
            		}
            	}
            }
            
            //results = file name to print query results to
            //if args has a results flag
            if (argumentParser.hasFlag(RESULTS_FLAG))
            {
            	if (argumentParser.getValue(RESULTS_FLAG) != null)
            	{
            		resultsFile = Paths.get(argumentParser.getValue(RESULTS_FLAG));
            		//if results flag is not valid
            		if (!resultsFile.isAbsolute())
            		{
            			System.err.println("Invalid results file");
            		}
            	}
            	//if no results file path provided
            	else
            	{
            		resultsFile = Paths.get(RESULTS_DEFAULT);
            	}
            }


            //Traverses through the directory given by user
            InvertedIndexBuilder.traverse(directory, invertedIndex);
            
            if (argumentParser.hasFlag(INDEX_FLAG))
            {
            	 //Writes to the appropriate text file, if provided
                invertedIndex.writeIndexToFile(outputFile.toString());
            }
            
            if (argumentParser.hasFlag(QUERIES_FLAG))
            {
            	queryParser.parseFile(queriesFile.toString(), resultsFile.toString());
            }
            
            if(argumentParser.hasFlag(RESULTS_FLAG))
            {
            	queryParser.writeToFile(resultsFile.toString());
            }
            

        } catch (NullPointerException e) { 
        	System.err.println("No input found");
        }
        
    }
}
