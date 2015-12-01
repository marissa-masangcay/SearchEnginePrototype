import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class instantiates a private LinkedHashMap that will store the provided queries
 * given through the command line arguments, assuming that the input is valid.
 * It also stores a private map that stores the given queries matched with the
 * appropriate search result objects.
 */
public class ThreadedQueryParser {


	/**Initializes a LinkedHashMap to store query lines and matching
	 * search results*/
	private final LinkedHashMap<String, List<SearchResult>> results;

	/**Initializes an inverted index*/
	private final InvertedIndex invertedIndex;
	
	/** Work queue used to handle multithreading for this class. */
	private final WorkQueue workers;
	
	private static final Logger logger = LogManager.getLogger();
	private int pending;


	/**Initializes a Query Parser object as well as an empty results map
		  and an inverted index*/
	public ThreadedQueryParser(InvertedIndex inputInvertedIndex, int numberOfThreads)
	{
		results = new LinkedHashMap<String, List<SearchResult>>();
		invertedIndex = inputInvertedIndex;
		workers = new WorkQueue(numberOfThreads);  
        pending = 0;
	}
	
	/**
     * Shutsdown the work queue after all pending work is finished. After this
     * point, all additional calls to {@link #parseTextFiles(Path, String)} will
     * no longer work.
     */
    public synchronized void shutdown() {
        logger.debug("Shutting down");
        finish();
        workers.shutdown();
    }
    
    /**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				logger.debug("Waiting until finished");
				this.wait();
			}
		}
		catch (InterruptedException e) {
			logger.debug("Finish interrupted", e);
		}
	}
	
	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	private synchronized void incrementPending() {
		pending++;
		logger.debug("Pending is now {}", pending);
	}
	
	
	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending() {
		pending--;
		logger.debug("Pending is now {}", pending);

		if (pending <= 0) {
			this.notifyAll();
		}
	}


	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param path
	 *            file to read in for queries
	 * @param outputPath
	 *            file to write search result objects to
	 * @return 
	 */
	public void parseFile(String path) throws IOException
	{			
		Path inputPath = Paths.get(path);

		try (BufferedReader bufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8))
		{
			String line;

			//Reads in each line of file
			while ( (line = bufferedReader.readLine()) != null ) 
			{
//				//iterates through lines of queries from files
//				parseLine(line);
				workers.execute(new QueryMinion(line));
			}
		}
	} 

	/**
	 * Reads in a line and adds them to the results map with 
	 * the appropriate search result and adds lines to lines map
	 *
	 * @param line
	 *            line to add to lines map and results map
	 * @throws IOException
	 * @return 
	 */
	public void parseLine(String line) throws IOException
	{
		List<SearchResult> partialSearch;

		String[] cleanedSplitLine = InvertedIndexBuilder.split(line);
		partialSearch = invertedIndex.partialSearch(cleanedSplitLine);
		synchronized( results )
		{
			results.put(line, partialSearch);
		}
	}

	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param outputPath
	 *            file to write search results to as JSON objects
	 * @throws IOException
	 * @return 
	 */ 
	public void writeToFile(String outputPath) throws IOException 
	{
		try (	
				BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outputPath) , StandardCharsets.UTF_8);
				)
		{
			JSONWriter.resultsToJSON(bufferedWriter, results);
		}

	}
	
	
	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class QueryMinion implements Runnable {

		private String line;

		public QueryMinion(String line) {
			logger.debug("Minion created for {}", line);
			this.line = line;
			incrementPending();
		}

		@Override
		public void run() {
			try {
				parseLine(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			decrementPending();
		}
		

	}
	
//	@Override 
//	public boolean add(E element)
//	{
//		lock.lockReadWrite();
//		try{
//			return super.add(E element);
//		}
//		finally{
//			unlock.lockReadWrite();
//		}
//	}

}

