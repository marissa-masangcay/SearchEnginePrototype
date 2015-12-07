import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class has a parse file method that is used by both
 * the QueryParser and ThreadedQueryParser classes to avoid
 * code duplication. It also has the parseLine and writeToFile
 * methods to be extended by the two classes.
 */
public abstract class AbstractQueryParser {

	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param path
	 *            file to read in for queries
	 * @param outputPath
	 *            file to write search result objects to
	 * @return 
	 */
	public void parseFile(String path) throws IOException
	{			
		Path inputPath = Paths.get(path);
		
		try (BufferedReader bufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8))
		{
			String line;
			
			//Reads in each line of file
			while ( (line = bufferedReader.readLine()) != null ) 
			{
				//iterates through lines of queries from files
				parseLine(line);
			}
		}
	}
	
	public abstract void parseLine(String line) throws IOException;
	
	public abstract void writeToFile(String outputPath) throws IOException;

	
	
}
