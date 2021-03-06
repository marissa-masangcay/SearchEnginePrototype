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
        
        ArgumentParser argumentParser = new ArgumentParser(args);
        
        InvertedIndex invertedIndex;
        AbstractQueryParser queryParser;
        
        ThreadedInvertedIndex threadedInvertedIndex;
        ThreadedIndexBuilder threadedIndexBuilder;
        ThreadedQueryParser threadedQueryParser;
        
        Search webCrawler;
               
        String directoryToTraverse = null;
        String url = null;
        
        Path directory = null;
        Path outputFile = null;
        Path resultsFile = null;
        Path queriesFile = null;
        
        int numberOfThreads = -1;
        
        try{  

        	/**thread = number of threads to use*/
        	/** if multi-threaded */
        	if ( argumentParser.hasFlag(THREAD_FLAG) || argumentParser.hasFlag(SEED_FLAG))
        	{
        		try {
        			numberOfThreads = Integer.parseInt(argumentParser.getValue(THREAD_FLAG));
        			if ( numberOfThreads == 0 )
        			{
        				numberOfThreads = THREAD_DEFAULT;
        			}
        		} catch ( NumberFormatException e ) {
        			numberOfThreads = THREAD_DEFAULT;
        		}
        		catch ( NullPointerException e ) {
        			numberOfThreads = THREAD_DEFAULT;
        		}

        		url = argumentParser.getValue(SEED_FLAG);
        		
        		threadedInvertedIndex = new ThreadedInvertedIndex();
        		invertedIndex = threadedInvertedIndex;
        		threadedIndexBuilder = new ThreadedIndexBuilder(numberOfThreads);
        		threadedQueryParser = new ThreadedQueryParser(threadedInvertedIndex, numberOfThreads);
        		queryParser = threadedQueryParser;
        		webCrawler = new Search(threadedInvertedIndex);

        		if ( argumentParser.hasFlag(INPUT_FLAG) )
        		{
        			//if input flag has a value
        			if ( argumentParser.hasValue(INPUT_FLAG) )
        			{
        				directoryToTraverse = argumentParser.getValue(INPUT_FLAG);
        				directory = Paths.get(directoryToTraverse);

        				//if directory isn't a valid directory
        				if ( !Files.isDirectory(directory) )
        				{
        					System.err.println("Invalid directory");
        				}
        				threadedIndexBuilder.traverse(directory, threadedInvertedIndex);
        				threadedIndexBuilder.finish();
        			}
        			else //if input flag has no value
        			{
        				System.err.println("No directory entered, please enter valid directory");
        			}
        		}
        		//if args has no input flag
//        		else
//        		{
//        			System.err.println("No directory found, please enter a directory");
//        		}
        		
        		
        		if ( argumentParser.hasFlag(SEED_FLAG))
        		{
        			webCrawler.startSearch(url);
        			webCrawler.finish();
        		}


        		/**query = file name to obtain queries from*/
        		if ( argumentParser.hasFlag(QUERIES_FLAG) )
        		{
        			if ( argumentParser.getValue(QUERIES_FLAG) != null )
        			{
        				queriesFile = Paths.get(argumentParser.getValue(QUERIES_FLAG));
        				//if queries flag is not valid
        				if ( !Files.isReadable(queriesFile) )
        				{
        					System.err.println("Invalid query file");
        				}
        				threadedQueryParser.parseFile(queriesFile.toString());
        				threadedQueryParser.finish();
        			}
        		}       

        		threadedIndexBuilder.shutdown();
        		threadedQueryParser.shutdown();
        		webCrawler.shutdown();
        	}
        	else{

        		invertedIndex = new InvertedIndex();
        		queryParser = new QueryParser(invertedIndex);

        		/**input = directory to traverse through*/
        		if ( argumentParser.hasFlag(INPUT_FLAG) )
        		{
        			//if input flag has a value
        			if ( argumentParser.hasValue(INPUT_FLAG) )
        			{
        				directoryToTraverse = argumentParser.getValue(INPUT_FLAG);
        				directory = Paths.get(directoryToTraverse);

        				//if directory isn't a valid directory
        				if ( !Files.isDirectory(directory) )
        				{
        					System.err.println("Invalid directory");
        				}
        				//Traverses through the directory given by user
        				InvertedIndexBuilder.traverse(directory, invertedIndex);
        			}
        			else //if input flag has no value
        			{
        				System.err.println("No directory entered, please enter valid directory");
        			}
        		}
        		//if args has no input flag
        		else
        		{
        			System.err.println("No directory found, please enter a directory");
        		}


        		/**query = file name to obtain queries from*/
        		if ( argumentParser.hasFlag(QUERIES_FLAG) )
        		{
        			if ( argumentParser.getValue(QUERIES_FLAG) != null )
        			{
        				queriesFile = Paths.get(argumentParser.getValue(QUERIES_FLAG));
        				//if queries flag is not valid
        				if ( !Files.isReadable(queriesFile) )
        				{
        					System.err.println("Invalid query file");
        				}
        				else
        				{
        					//parses files for queries
        					queryParser.parseFile(queriesFile.toString());
        				}
        			}
        		}     

        	}         
        	

        	/**index = file name to print to*/
        	if( argumentParser.hasFlag(INDEX_FLAG) )
        	{
        		outputFile = Paths.get(argumentParser.getOrDefault(INDEX_FLAG, INDEX_DEFAULT));
        		if ( !outputFile.isAbsolute() )
        		{
        			System.err.println("Invalid file");
        		}
        		invertedIndex.writeIndexToFile(outputFile.toString());
        	}


        	/**results = file name to print query results to*/
        	if ( argumentParser.hasFlag(RESULTS_FLAG) )
        	{
        		resultsFile = Paths.get(argumentParser.getOrDefault(RESULTS_FLAG, RESULTS_DEFAULT));
        		if ( !resultsFile.isAbsolute() )
        		{
        			System.err.println("Invalid results file");
        		}
        		queryParser.writeToFile(resultsFile.toString());
        	}         


        } catch( NullPointerException e ) { 
        	System.err.println("No input found");
        } 

    }      
    
    
}
