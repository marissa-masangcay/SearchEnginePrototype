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

// TODO Re-indent everything because this is a mix of tabs spaces

/**
 * This class instantiates a private LinkedHashMap that will store the provided queries
 * given through the command line arguments, assuming that the input is valid.
 * It also stores a private map that stores the given queries matched with the
 * appropriate search result objects.
 */
public class ThreadedQueryParser extends AbstractQueryParser {

	/**Initializes a LinkedHashMap to store query lines and matching
	 * search results*/
	private final LinkedHashMap<String, List<SearchResult>> results;

	/**Initializes an inverted index*/
	private final ThreadedInvertedIndex invertedIndex; 
	
	/** Work queue used to handle multithreading for this class. */
	private final WorkQueue workers;
	
	private static final Logger logger = LogManager.getLogger();
	private int pending;
	private final ReadWriteLock lock; 


	/**Initializes a Query Parser object as well as an empty results map
		  and an inverted index*/
	public ThreadedQueryParser(ThreadedInvertedIndex inputInvertedIndex, int numberOfThreads)
	{
		results = new LinkedHashMap<String, List<SearchResult>>();
		invertedIndex = inputInvertedIndex;
		workers = new WorkQueue(numberOfThreads);  
		pending = 0; 
		lock = new ReadWriteLock();
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
			while ( pending > 0 ) {
				logger.debug("Waiting until finished");
				this.wait();
			}
		}
		catch ( InterruptedException e ) {
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

		if ( pending <= 0 ) {
			this.notifyAll();
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
	

	@Override
	public void parseLine(String line) throws IOException
	{
		lock.lockReadWrite();
		results.put(line, null);
		lock.unlockReadWrite();
		//iterates through lines of queries from files
		workers.execute(new QueryMinion(line));
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
	@Override
	public void writeToFile(String outputPath) throws IOException 
	{
		try (	
				BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outputPath) , StandardCharsets.UTF_8);
				)
		{
			// TODO Only need to lock for read, because reading from the shared data
			lock.lockReadWrite();
			JSONWriter.resultsToJSON(bufferedWriter, results);
			lock.unlockReadWrite(); 
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
			lock.lockReadOnly();; // TODO Remove this from here
				try {
					List<SearchResult> partialSearch;

					String[] cleanedSplitLine = InvertedIndexBuilder.split(line);
					partialSearch = invertedIndex.partialSearch(cleanedSplitLine);
					
					// TODO Protect this put by a lock for writing.
					results.put(line, partialSearch);
					
				} catch (IOException e) {
					// TODO No stack trace
					e.printStackTrace();
				} 
				finally {
					lock.unlockReadOnly();
				}
				decrementPending();
		}	
	}
}
	



