import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedIndexBuilder {
	
	/** Work queue used to handle multithreading for this class. */
	private final WorkQueue workers;
	
	private static final Logger logger = LogManager.getLogger();
	private int pending;
	
	
	/**
     * Initializes this multithreaded file parser. The underlying work queue
     * will be active until {@link #shutdown()} is called.
     */
    public ThreadedIndexBuilder() {
        workers = new WorkQueue();  
        pending = 0;
    }

}
