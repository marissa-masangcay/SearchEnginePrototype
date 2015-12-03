import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class initializes a multithreaded file parser. 
 */
public class ThreadedIndexBuilder {
	
	/** Work queue used to handle multithreading for this class. */
	private final WorkQueue workers;
	private static final Logger logger = LogManager.getLogger();
	private int pending;
	ReadWriteLock lock;
	
	
	/**
     * Initializes this multithreaded file parser. The underlying work queue
     * will be active until {@link #shutdown()} is called.
     */
    public ThreadedIndexBuilder(int numberOfThreads) {
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
     * Counts the number of words that match the provided regular expression
     * within all of the text files found at the provided path.
     *
     * @param path   path to search (can be a directory or text file)
     * @param regex  regular expression to match words against
     * @see TextFileWordMatcher#countWords(Path, String)
     */
    public void traverse(Path path, InvertedIndex invertedIndex) throws IOException {

        if ( Files.isDirectory(path) ) {
            try (
                DirectoryStream<Path> directory = Files.newDirectoryStream(path);
            ) {
                for ( Path current : directory ) {
                    traverse(current, invertedIndex);
                }
            }
            catch ( IOException e ) {
                System.err.println(e.getMessage());
            }
        }
        else if ( Files.isReadable(path) && path.toString().toLowerCase().endsWith(".txt") ) {
            workers.execute(new FileMinion(path, invertedIndex));
        }
    }  
    
    /**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class FileMinion implements Runnable {

		private Path file;
		private InvertedIndex invertedIndex;

		public FileMinion(Path file, InvertedIndex invertedIndex) {
			logger.debug("Minion created for {}", file);
			this.file = file;
			this.invertedIndex = invertedIndex;
			incrementPending();
		}

		@Override
		public void run() {
			lock.lockReadWrite();{
				try {
					InvertedIndexBuilder.parseFile(file.toString(), invertedIndex);
				} catch ( IOException e ) {
					e.printStackTrace();
				}
				finally{
					lock.unlockReadWrite();
				}
			}
			decrementPending();
		}
	}

}
