import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.NavigableSet;


public class FileParser {

	
	/** Stores all words found without special characters as keys in outermost map,
	  * Stores the text files found with the associated key as the value for the 
	  * outermost map and as the key for the second nested map.
	  * Stores the positions of the word found in that given text file as the
	  * value for the text file in the second nested map.
	  */
	private static TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	
	/** Instantiates the inverted index */
	public FileParser()
	{
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	
	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	
	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	
	/**
	 * Helper method to indent several times by 2 spaces each time. For example,
	 * indent(0) will return an empty string, indent(1) will return 2 spaces,
	 * and indent(2) will return 4 spaces.
	 * 
	 * @param times
	 * @return
	 * @throws IOException
	 */
	public static String indent(int times) throws IOException {
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}

	
	/**
	 * Helper method to quote text for output. This requires escaping the
	 * quotation mark " as \" for use in Strings. 
	 * 
	 * @param text 
	 *            input to surround with quotation marks
	 * @return quoted text
	 */
	public static String quote(String text) {
		return "\"" + text + "\"";
	}

	
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
	public static void add(String word, String text, int position)
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
	public static void readFile(String file){
		int position = 0;

		try {
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
	 */
	public static void writeIndex(String file)
	{
		writeNestedObject(file, index);

	}

	
	/**
	 * Writes the elements as a JSON object with nested array values to the
	 * specified output path using the "UTF-8" character set. The output is in a
	 * "pretty" format with 2 spaces per indent level.
	 * 
	 * <pre>
	 * {
	 *   "key1: {
	 *     "value1/key1": [
	 *       value1,
	 *       value2
	 *     ],
	 *     "value2/key2": [
	 *       value3
	 *     ]
	 *   }
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Note that there is not a trailing space after the second value, the key
	 * should be in quotes, and this method should NOT throw an exception.
	 * </p>
	 * 
	 * @param output
	 *            file to write to
	 * @param elements
	 *            to write as a JSON array
	 * @return true if there were no problems or exceptions
	 */
	public static void writeNestedObject(String output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		FileWriter fileWriter;
		File inputFile = new File(output);

		try{

			fileWriter = new FileWriter(inputFile);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(inputFile), "UTF8"));

			bufferedWriter.write("{");

			//if elements is not empty
			if(!elements.isEmpty())
			{
				//Starts with first element in outer most map (key = word)
				Entry<String, TreeMap<String, TreeSet<Integer>>> first = elements.firstEntry();

				//For loop traverses through and writes words stored in inverted index
				int wordCounter = 0;
				for (Entry<String, TreeMap<String, TreeSet<Integer>>> entry: elements.tailMap(first.getKey(), true).entrySet())
				{
					bufferedWriter.write(System.lineSeparator());
					bufferedWriter.write(indent(1));
					bufferedWriter.write(quote(entry.getKey()));
					bufferedWriter.write(":");
					bufferedWriter.write(" ");
					bufferedWriter.write("{");

					int textCounter = 0;
					//For loop traverses through and writes text file names stored in inverted index
					for(Entry<String, TreeSet<Integer>> secondEntry: entry.getValue().entrySet())
					{
						bufferedWriter.write(System.lineSeparator());
						bufferedWriter.write(indent(2));
						bufferedWriter.write(quote(secondEntry.getKey()));
						bufferedWriter.write(":");
						bufferedWriter.write(" ");
						bufferedWriter.write("[");

						int positionCounter = 0;
						//For loop traverses through and writes positions stored in inverted index
						for(Integer thirdEntry: secondEntry.getValue().tailSet(secondEntry.getValue().iterator().next(), true))
						{
							if(positionCounter!=0 && positionCounter< secondEntry.getValue().size())
							{
								bufferedWriter.write(",");
							}
							bufferedWriter.write(System.lineSeparator());
							bufferedWriter.write(indent(3)+thirdEntry);
							positionCounter++;
						}
						
						bufferedWriter.write(System.lineSeparator());
						bufferedWriter.write(indent(2));
						bufferedWriter.write("]");
						textCounter++;
						if(entry.getValue().size()>1 && textCounter<entry.getValue().size())
						{
							bufferedWriter.write(",");
						}
						
					}
					bufferedWriter.write(System.lineSeparator());
					bufferedWriter.write(indent(1));
					bufferedWriter.write("}");
					wordCounter++;
					if(elements.size()>1 && wordCounter<elements.size())
					{
						bufferedWriter.write(",");
					}
					
				}
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(0));
			}
			
			bufferedWriter.write("}");
			bufferedWriter.close();	
		}catch (IOException e){
			System.err.println("Problem writing to the file: "+output);
		}
	}

}

