import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Search {
	
	//Pattern..addAll
	
	//test
	
	private HashSet<URL> linkSet;
	private final WorkQueue workers;
	private final ReadWriteLock lock;
	private static final Logger logger = LogManager.getLogger();
	private URL URL;
	public ThreadedInvertedIndex invertedIndex;
	
	public Search(String url, ThreadedInvertedIndex invertedIndex)
	{
		try {
			URL = new URL (url);
		} catch (MalformedURLException e) {
			System.err.println("Invalid seed URL");
			e.printStackTrace();
		}
		linkSet = new HashSet<URL>();
		workers = new WorkQueue();
		lock = new ReadWriteLock();
		this.invertedIndex = invertedIndex;
		workers.execute(new LinkMinion(URL, invertedIndex));
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
				workers.execute(new LinkMinion(link, invertedIndex));
			}
		}
	}
	
	
//	public void run(String seedURL) throws UnknownHostException, MalformedURLException, IOException
//	{
//			String html = HTTPFetcher.fetchHTML(seedURL);
//			URL baseURL = new URL(seedURL);
//			//URL absolute = new URL(baseURL, "../index.html");
//			ArrayList<URL> links = LinkParser.listLinks(baseURL, html);
//			
//			lock.lockReadWrite();
//			for(int i = 0; i<links.size(); i++)
//			{
//				URLHelper(links.get(i));
//			}
//			lock.unlockReadWrite();
//			
//			String text = HTMLCleaner.cleanHTML(html);
//			
//			HTMLCleaner.parseWords(text, invertedIndex);		
//			
//			
//	}

	
	
	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class LinkMinion implements Runnable {

		private URL seedURL;
		private ThreadedInvertedIndex invertedIndex;

		public LinkMinion(URL seedURL, ThreadedInvertedIndex invertedIndex) 
		{
			logger.debug("Minion created for {}", seedURL);
			this.seedURL = seedURL;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			String html = null;
			try {
				html = HTTPFetcher.fetchHTML(seedURL.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			URL baseURL = null;
			URL absolute = null;
			try {
				baseURL = new URL(seedURL.toString());
				absolute = new URL(baseURL, "../index.html");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//ArrayList<URL> links = LinkParser.listLinks(baseURL, html);
			ArrayList<URL> links = LinkParser.listLinks(absolute, html);
			
			lock.lockReadWrite();
			for(int i = 0; i<links.size(); i++)
			{
				URLHelper(links.get(i));
			}
			lock.unlockReadWrite();
			
			String text = HTMLCleaner.cleanHTML(html);
			
			local = HTMLCleaner.parseWords(text);
			
			invertedIndex.addAll(local);		
						    

		}	
	}
	
	

}
