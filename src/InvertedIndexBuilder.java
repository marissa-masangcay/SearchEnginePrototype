import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvertedIndexBuilder {


	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";


	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";


	/**
	 * Cleans a word by converting it to lower case and removing any whitespace
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
	 * lower case without any special characters, or an empty array if the cleaned
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
		if (!text.isEmpty() )
		{
			textSplit = text.split(SPLIT_REGEX);
		}
		return textSplit;
	}


	/**
	 * Reads in a file to parse words and add them at their positions found
	 * along with text file's name to the inverted index. 
	 *
	 * @param file
	 *            file to read in
	 * @return 
	 */
	public static void parseFile(String path, InvertedIndex invertedIndex) throws IOException
	{
		int position = 0;

		try(BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(path), "UTF8")))
		{
			Path inputFile = Paths.get(path);
			String line;

			//Reads in each line of file
			while ( (line = bufferedReader.readLine()) != null ) 
			{
				String[] splitLine = split(line);
				for (int i = 0; i < splitLine.length; i++ )
				{
					String word = clean(splitLine[i]);
					if ( !word.isEmpty() )
					{
						//adds word, text file name, and position 
						//to the inverted index
						position++;
						invertedIndex.add(word, inputFile.toString(), position);
					}
				}
			}
		}
	} 
	
	/**
	 * Traverses the directory passed in from args to attempt to 
	 * find and extract text files from.
	 * Uses {@link #fileParser.invertedIndexBuilder(String file)} to 
	 * read and add to the map each text file found.
	 *
	 * @param directory input directory from command line args
	 * @return 
	 * @throws IOException 
	 * @see #fileParser.invertedIndexBuilder(String file)
	 */
	public static void traverse(Path directory, InvertedIndex invertedIndex) throws IOException{

		//Executed if directory is a directory	
		if ( Files.isDirectory(directory) )
		{
			try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory))
			{
				for ( Path directoryPaths: directoryStream )
				{
					traverse(directoryPaths, invertedIndex);
				}	
			}
		}
		//Executed if a file
		else
		{
			String fileName = directory.toString().toLowerCase();

			if ( fileName.endsWith("txt") )
			{
				//Reads file and adds words read to inverted index data structure
				InvertedIndexBuilder.parseFile(directory.toString(), invertedIndex);
			}
		}
	}

}
