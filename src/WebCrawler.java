import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class WebCrawler {
	
	//Pattern..addAll
	
	private HashSet<URL> linkSet;
	private final WorkQueue workers;
	private final ReadWriteLock lock;
	private static final Logger logger = LogManager.getLogger();
	
	public WebCrawler()
	{
		linkSet = new HashSet<URL>();
		workers = new WorkQueue();
		lock = new ReadWriteLock();
	}
	
	private void URLHelper(URL link)
	{
		if ( linkSet.size()< 50 )
		{
			if ( !linkSet.contains(link) )
			{
				lock.lockReadWrite();
				linkSet.add(link);
				lock.unlockReadWrite();
				workers.execute(new LinkMinion(link));
			}
		}
	}
	
	
	public void run(String seedURL) throws UnknownHostException, MalformedURLException, IOException
	{
			String html = HTTPFetcher.fetchHTML(seedURL);
			URL baseURL = new URL(seedURL);
			ArrayList<URL> links = LinkParser.listLinks(baseURL, html);
			
			lock.lockReadWrite();
			for(int i = 0; i<links.size(); i++)
			{
				workers.execute(new LinkMinion(links.get(i)));
			}
			lock.unlockReadWrite();
			
			String text = HTMLCleaner.cleanHTML(html);
			
			
			
			
	}

	
	
	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class LinkMinion implements Runnable {

		private URL link;
		private InvertedIndex invertedIndex;

		public LinkMinion(URL link) 
		{
			logger.debug("Minion created for {}", link);
			this.link = link;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {
			
//			try {
//				//TODO
//
//			} catch (IOException e) {
//				System.err.println("Error in work queue at Partial Search");
//			} 
		}	
	}
	
	

}
