import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;


public class InvertedIndex {

	/** Stores all words found without special characters as keys in outermost map,
	  * Stores the text files found with the associated key as the value for the 
	  * outermost map and as the key for the second nested map.
	  * Stores the positions of the word found in that given text file as the
	  * value for the text file in the second nested map.
	  * Stores the queries in a map with the queries read in from given file
	  * as the string key value and the search result generated as the value
	  */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	
	/** Instantiates the inverted index*/
	public InvertedIndex()
	{
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
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
	public void add(String word, String text, int position)
	{

		if ( !index.containsKey(word) ) {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
		}

		if ( !index.get(word).containsKey(text) ) {
			index.get(word).put(text, new TreeSet<Integer>());
		}

		index.get(word).get(text).add(position);

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
	public void writeIndexToFile(String output) throws UnsupportedEncodingException, FileNotFoundException, IOException{
		Path inputFile = Paths.get(output);

		try(
				BufferedWriter bufferedWriter = Files.newBufferedWriter(inputFile, StandardCharsets.UTF_8);
				)
		{
			JSONWriter.toJSON(inputFile.toString(), index, bufferedWriter);
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
		return index.containsKey(word);
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
		if ( index.get(word).containsValue(path) )
		{
			return true;
		}
		else
		{
			return false;
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
		for ( int i = 0; i < wordsToAdd.size(); i++ )
		{
			index.put(wordsToAdd.get(i), null);
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
		return index.toString();
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
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> queryMap = new HashMap<String, SearchResult>();
		
		String fileName = null;
		int frequency = 0;
		int initialPosition = 0;	

		for ( String query: queries )
		{
			for ( String word: index.tailMap(query).keySet() )
			{
				if ( word.startsWith(query) )
				{
					for ( String path : index.get(word).keySet() )
					{
						fileName = path;
						frequency = index.get(word).get(path).size();
						initialPosition = index.get(word).get(path).first();

						if ( queryMap.containsKey(fileName) )
						{	
							//updates search result's frequency and initial position as needed
							queryMap.get(fileName).update(frequency, initialPosition);
						}
						else
						{
							SearchResult sr = new SearchResult(fileName, frequency, initialPosition);
							searchResults.add(sr);
							queryMap.put(fileName, sr);
						}
					}
				}
				else
				{
					break;
				}
			}
		}
		
		//sorts all search result objects created using custom comparator
		Collections.sort(searchResults);
		return searchResults;
	}
	
}

