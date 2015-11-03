import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * This class instantiates a private map that will store the user input
 * given through the command line arguments, assuming that the input is valid.
 */
public class QueryParser {
	
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

		try(BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(path), "UTF8")))
		{
			String line;

			//Reads in each line of file
			while ((line = bufferedReader.readLine()) != null) 
			{
				//iterates through lines of queries from files
				String[] splitLine = InvertedIndexBuilder.split(line);
				//cleans each word in array of queries
				for(int i = 0; i<splitLine.length; i++)
				{
					InvertedIndexBuilder.clean(splitLine[i]);
				}
				invertedIndex.partialSearch(splitLine);
			}
		}
	} 


}