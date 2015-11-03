import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;


public class InvertedIndex {

	
	/** Stores all words found without special characters as keys in outermost map,
	  * Stores the text files found with the associated key as the value for the 
	  * outermost map and as the key for the second nested map.
	  * Stores the positions of the word found in that given text file as the
	  * value for the text file in the second nested map.
	  */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final HashMap<String, SearchResult> queryMap;

	
	/** Instantiates the inverted index */
	public InvertedIndex()
	{
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		queryMap = new HashMap<String, SearchResult>();
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

		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
		}

		if (!index.get(word).containsKey(text)) {
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

		// TODO Files.newBufferedWriter(inputFile, Charset.forName("UTF8"))
		try(
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(inputFile.toString()), "UTF8"));
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
		if (index.get(word).containsValue(path))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// TODO addAll(ArrayList<String> words, Path file, int start)
	/**
	 * Adds all words within the given array list to the inverted index 
	 * 
	 * @param wordsToAdd
	 *            words to add to inverted index
	 */
	public void addAll(ArrayList<String> wordsToAdd)
	{
		for(int i = 0; i < wordsToAdd.size(); i++)
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
	
	
	public List<SearchResult> partialSearch(String[] queries)
	{
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		String fileName;
		int frequency;
		int initialPosition;
		String firstFileName;
		
		for(int i = 0; i<queries.length; i++)
		{
			if(index.containsKey(queries[i]))
			{
				firstFileName = index.get(queries[i]).firstEntry().getKey();
				for(Entry<String, TreeSet<Integer>> entry: index.get(queries[i]).tailMap(firstFileName, true).entrySet())
				{
					fileName = entry.getKey();
					frequency = entry.getValue().size();
					initialPosition = entry.getValue().first();
					if ( queryMap.containsKey(fileName) )
					{
						queryMap.get(fileName).setFrequency((frequency + queryMap.get(fileName).getFrequency()));
						if(queryMap.get(fileName).getInitialPosition()>initialPosition)
						{
							queryMap.get(fileName).setInitialPosition(initialPosition);
						}
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
				System.out.println("Index doesn't have: "+queries[i]);
			}
		}
		
		System.out.println("");
		for(int m = 0; m<queries.length; m++)
		{
			System.out.print(queries[m]+ " ");
		}
		
		Collections.sort(searchResults, SearchResults.ORDER_BY_SEARCH_RESULT);
		
		System.out.println("");
		for(int n = 0; n<searchResults.size(); n++)
		{
			System.out.println(searchResults.get(n));
		}
		
		queryMap.clear();
		
		
		return searchResults;
	}
}

