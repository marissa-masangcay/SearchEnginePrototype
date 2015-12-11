import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A simple work queue implementation. 
 * It is up to the user of this class to keep track of whether
 * there is any pending work remaining.
 */
public class WorkQueue {

	/** Pool of worker threads that will wait in the background until work is available. */
	private final PoolWorker[] workers;

	/** Queue of pending work requests. */
	private final LinkedList<Runnable> queue;

	/** Used to signal the queue should be shutdown. */
	private volatile boolean shutdown;

	/** The default number of threads to use when not specified. */
	public static final int DEFAULT = 5;
	
	private static final Logger logger = LogManager.getLogger();
	private int pending;


	/**
	 * Starts a work queue with the default number of threads.
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
		pending = 0;
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads) {
		this.queue   = new LinkedList<Runnable>();
		this.workers = new PoolWorker[threads];
		shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			workers[i] = new PoolWorker();
			workers[i].start();
		}
	}

	/**
	 * Adds a work request to the queue. A thread will process this request
	 * when available.
	 *
	 * @param r work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable r) {
		synchronized (queue) 
		{
			queue.addLast(r);
			queue.notifyAll();
			
			incrementPending();
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished,
	 * but threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		finish();
		shutdown = true;

		synchronized (queue) 
		{
			queue.notifyAll();
		}
	}
	
    /**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish() {
		synchronized(queue)
		{
			try {
				while ( pending > 0 ) {
					logger.debug("Waiting until finished");
					queue.wait();
				}
			}
			catch ( InterruptedException e ) {
				logger.debug("Finish interrupted", e);
			}
		}
	}

	
	/**
	 * Returns the number of worker threads being used by the work queue.
	 *
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}
	
	
	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	private void incrementPending() {
		synchronized(queue)
		{
			pending++;
			logger.debug("Pending is now {}", pending);
		}
	}
	
	
	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	private void decrementPending() {
		synchronized(queue)
		{
			pending--;
			logger.debug("Pending is now {}", pending);

			if ( pending <= 0 ) {
				queue.notifyAll();
			}
		}
	}

	
	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected,
	 * will exit instead of grabbing new work from the queue. These threads will
	 * continue running in the background until a shutdown is requested.
	 */
	private class PoolWorker extends Thread {

		@Override
		public void run() {
			Runnable r = null;

			while ( true ) {
				synchronized (queue)
				{
					while ( queue.isEmpty() && !shutdown ) {
						try {
							queue.wait();
						}
						catch ( InterruptedException ex ) {
							System.err.println("Warning: Work queue interrupted " +
									"while waiting.");
							Thread.currentThread().interrupt();
						}
					}

					if (shutdown) {
						break;
					}
					else {
						r = queue.removeFirst();
					}
				}

				try {
					r.run();
				}
				catch ( RuntimeException ex ) {
				    // catch runtime exceptions to avoid leaking threads
					System.err.println("Warning: Work queue encountered an " +
							"exception while running.");
				}
				finally{
					decrementPending();
				}
			}
		}
	}
}
