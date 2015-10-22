import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

	
	/** Instantiates the inverted index */
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
		
		//if index doesn't contain word
		if(!index.containsKey(word))
		{
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
			//if index doesn't contain text file name
			if(!index.get(word).containsKey(text))
			{
				index.get(word).put(text, new TreeSet<Integer>());
				//if index doesn't contain position number
				if(!index.get(word).get(text).contains(position))
				{
					index.get(word).get(text).add(position);
				}
			}
			//if index has text file name
			else if(index.get(word).containsKey(text))
			{
				//if index doesn't contain position for that text file name
				if(!index.get(word).get(text).contains(position))
				{
					index.get(word).get(text).add(position);
				}
			}
		}

		//if index contains word
		else if(index.containsKey(word))
		{
			//if index doesn't contain text file name
			if(!index.get(word).containsKey(text))
			{
				index.get(word).put(text, new TreeSet<Integer>());
				//if index doesn't contain position number within that text file
				if(!index.get(word).get(text).contains(position))
				{
					index.get(word).get(text).add(position);
				}
			}
			//if index contains text file name
			else if(index.get(word).containsKey(text))
			{
				//if index doesn't contain position number within that text file
				if(!index.get(word).get(text).contains(position))
				{
					index.get(word).get(text).add(position);
				}
			}
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
	public void writeIndexToFile(String output) throws UnsupportedEncodingException, FileNotFoundException, IOException{
		Path inputFile = Paths.get(output);

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
		return index.get(word).containsValue(path);
	}

	
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
	public String toString()
	{
		return index.toString();
	}


}

