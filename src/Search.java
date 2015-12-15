import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Search {
	
	//Pattern..addAll
	
	
	private HashSet<URL> linkSet;
	private final WorkQueue workers;
	private final ReadWriteLock lock;
	private static final Logger logger = LogManager.getLogger();
	private ThreadedInvertedIndex invertedIndex;
	
	public Search(ThreadedInvertedIndex invertedIndex)
	{
		linkSet = new HashSet<URL>();
		workers = new WorkQueue();
		lock = new ReadWriteLock();
		this.invertedIndex = invertedIndex;
	}
	
	private void URLHelper(URL link)
	{
		if ( linkSet.size()< 50 )
		{
			if ( !linkSet.contains(link))
			{
				lock.lockReadWrite();
				linkSet.add(link);
				lock.unlockReadWrite();
				workers.execute(new LinkMinion(link.toString()));
			}
		}
	}
	
	
	public void startSearch(String seedURL) throws UnknownHostException, MalformedURLException, IOException
	{
			String html = HTTPFetcher.fetchHTML(seedURL);
			URL baseURL = new URL(seedURL);
			URL absolute = new URL(baseURL, "../index.html");
			ArrayList<URL> links = LinkParser.listLinks(baseURL, html);
			
			lock.lockReadWrite();
			for(int i = 0; i<links.size(); i++)
			{
				URLHelper(links.get(i));
			}
			lock.unlockReadWrite();
			
			String request = HTTPFetcher.craftHTTPRequest(baseURL, HTTPFetcher.HTTP.GET);
			
			ArrayList<String> lines = HTTPFetcher.fetchLines(baseURL, request);
			
			ThreadedInvertedIndex local = new ThreadedInvertedIndex();
			lock.lockReadWrite();
			int position = 1;
			for ( int j = 0; j < lines.size(); j++)
			{
				ArrayList<String> words = HTMLCleaner.parseWords(lines.get(j));
				for ( int k = 0; k < words.size(); k++)
				{
					local.add(words.get(k), seedURL, position);
					position++;
				}
			}
			lock.unlockReadWrite();
			
			invertedIndex.addAll(local);
			
	}
	
	
	 /**
     * Shutsdown the work queue after all pending work is finished. After this
     * point, all additional calls to {@link #parseTextFiles(Path, String)} will
     * no longer work.
     */
    public synchronized void shutdown() {
        logger.debug("Shutting down");
        workers.shutdown();
    }
    
    /**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish() {
		logger.debug("Finishing");
		workers.finish();
	}
	
	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class LinkMinion implements Runnable {
		
		String baseURL;

		public LinkMinion(String baseURL) 
		{
			logger.debug("Minion created for {}", baseURL);
			this.baseURL = baseURL;
		}

		@Override
		public void run() {
			
				try {
					startSearch(baseURL);
				} catch (IOException e) {
					System.err.println("Problem in link minion calling start search");
					e.printStackTrace();
				}					    
		}
	}	
	

}
