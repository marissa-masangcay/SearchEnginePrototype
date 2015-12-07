import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class initializes a multithreaded inverted index which extends
 * the inverted index class. 
 */
public class ThreadedInvertedIndex extends InvertedIndex {
	
	private final ReadWriteLock lock;
	
	/** Instantiates the inverted index*/
	public ThreadedInvertedIndex()
	{
		super();
		lock = new ReadWriteLock();
	}
	
	/**
	 * Properly adds a word, text file name, and position to the index.
	 *  Must initialize inner data structures if necessary. 
	 *
	 * @param word
	 *            word to add to index
	 * @param text
	 *            name of text file word was found in
	 * @param position
	 *            position word was found
	 * @return true if this was a unique entry, false if no changes were made to
	 *         the index
	 */
	@Override
	public void add(String word, String text, int position)
	{
		lock.lockReadWrite();
		try{
			super.add(word, text, position);
		}
		finally{
			lock.unlockReadWrite();
		}
	}
	
	
	/**
	 * Writes the elements as a JSON object with nested array values to the
	 * specified output path. 
	 * 
	 * @param output
	 *            file to write to
	 * @param elements
	 *            to write as a JSON array
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public void writeIndexToFile(String output) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		lock.lockReadOnly();
		try{
			super.writeIndexToFile(output);
		}
		finally{
			lock.unlockReadOnly();
		}
	}

	
	/**
	 * Checks to see if the inverted index contains word 
	 * 
	 * @param word
	 *            word to check
	 * @return true if index contains word
	 */
	public boolean hasWord(String word)
	{
		lock.lockReadOnly();
		try{
			return super.hasWord(word);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
	
	/**
	 * Checks to see if given path is contained within given word 
	 * 
	 * @param word
	 *            word to check to see if it contains the given path
	 * @param path
	 *            path to check for in given word
	 * @return true if path was found in word's map
	 */
	public boolean hasPath(String word, String path)
	{
		lock.lockReadOnly();
		try{
			return super.hasPath(word, path);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	

	/**
	 * Adds all words within the given array list to the inverted index 
	 * 
	 * @param wordsToAdd
	 *            words to add to inverted index
	 */
	public void addAll(ArrayList<String> wordsToAdd)
	{
		lock.lockReadWrite();
		try{
			super.addAll(wordsToAdd);
		}
		finally{
			lock.unlockReadWrite();
		}
	}
	
	/**
	 * Prints inverted index to string 
	 * 
	 * @return inverted index converted to string
	 */
	@Override
	public String toString() 
	{
		lock.lockReadOnly();
		try{
			return super.toString();
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Finds all words in inverted index that start with given query
	 * words with helper method wordsThatStartWithQuery and creates
	 * search result objects from those words and stores them all in
	 * a list to return 
	 * 
	 * @param queries
	 *            word to search for in inverted index
	 * @return List of search result objects that start with given queries
	 */
	public List<SearchResult> partialSearch(String[] queries) throws IOException
	{
		lock.lockReadOnly();
		try{
			return super.partialSearch(queries);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
	public void addAll(InvertedIndex other) {
		lock.lockReadWrite();
		try{
			super.addAll(other);
		}
		finally{
			lock.unlockReadWrite();
		}
	}


}
