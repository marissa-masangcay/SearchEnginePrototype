import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
	private final JSONWriter jsonWriter;

	
	/** Instantiates the inverted index */
	public InvertedIndex()
	{
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		jsonWriter = new JSONWriter();
	}

	
	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	
	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";


	
	/**
	 * Cleans a word by converting it to lowercase and removing any whitespace
	 * at the start or end of the word.
	 * 
	 * @param word
	 *            word to clean
	 * @return cleaned word
	 */
	public static String clean(String text) {
		text = text.toLowerCase().trim();
		text = text.replaceAll(CLEAN_REGEX,"");
		return text;
	}

	
	/**
	 * First cleans text. If the result is non-empty, splits the cleaned text
	 * into words by whitespace. The result will be an array of words in all 
	 * lowercase without any special characters, or an empty array if the cleaned
	 * text was empty.
	 * 
	 * @param text
	 *            input to clean and split into words
	 * @return array of words (or an empty array if cleaned text is empty)
	 * 
	 * @see #clean(String)
	 * @see #SPLIT_REGEX
	 */
	public static String[] split(String text) {
		String[] textSplit = new String[0];
		text = clean(text);
		if(!text.isEmpty())
		{
			textSplit = text.split(SPLIT_REGEX);
		}
		return textSplit;
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
		//Cleans word
		String cleanedWord = clean(word);

		//if index doesn't contain word
		if(!index.containsKey(cleanedWord))
		{
			index.put(cleanedWord, new TreeMap<String, TreeSet<Integer>>());
			//if index doesn't contain text file name
			if(!index.get(cleanedWord).containsKey(text))
			{
				index.get(cleanedWord).put(text, new TreeSet<Integer>());
				//if index doesn't contain position number
				if(!index.get(cleanedWord).get(text).contains(position))
				{
					index.get(cleanedWord).get(text).add(position);
				}
			}
			//if index has text file name
			else if(index.get(cleanedWord).containsKey(text))
			{
				//if index doesn't contain position for that text file name
				if(!index.get(cleanedWord).get(text).contains(position))
				{
					index.get(cleanedWord).get(text).add(position);
				}
			}
		}

		//if index contains word
		else if(index.containsKey(cleanedWord))
		{
			//if index doesn't contain text file name
			if(!index.get(cleanedWord).containsKey(text))
			{
				index.get(cleanedWord).put(text, new TreeSet<Integer>());
				//if index doesn't contain position number within that text file
				if(!index.get(cleanedWord).get(text).contains(position))
				{
					index.get(cleanedWord).get(text).add(position);
				}
			}
			//if index contains text file name
			else if(index.get(cleanedWord).containsKey(text))
			{
				//if index doesn't contain position number within that text file
				if(!index.get(cleanedWord).get(text).contains(position))
				{
					index.get(cleanedWord).get(text).add(position);
				}
			}

		}
	}


	/**
	 * Reads in a file to parse words and add them at their positions found
	 * along with text file's name to the inverted index. 
	 *
	 * @param file
	 *            file to read in
	 * @return 
	 */
	public void invertedIndexBuilder(String file){
		int position = 0;

		try{
			File inputFile = new File(file);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(file), "UTF8"));
		
			String line;
			//Reads in each line of file
			while ((line = bufferedReader.readLine()) != null) 
			{
				String[] splitLine = split(line);
				for(int i = 0; i<splitLine.length; i++)
				{
					String word = clean(splitLine[i]);
					if(!word.isEmpty())
					{
						//adds word, text file name, and position 
						//to the inverted index
						position++;
						add(word, inputFile.getPath(), position);
					}
				}

			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Writes the objects stored in the inverted index to the given
	 * file name/path provided in the parameter. 
	 *
	 * @param file
	 *            file to write inverted index to
	 * @return 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public void writeIndex(String file) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		writeIndexToFile(file, index);
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
	public void writeIndexToFile(String output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) throws UnsupportedEncodingException, FileNotFoundException, IOException{
		File inputFile = new File(output);

		try(
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(inputFile), "UTF8"));
				)

		{
			jsonWriter.toJSON(inputFile.toString(), index, bufferedWriter);
		}

	}

	

}

