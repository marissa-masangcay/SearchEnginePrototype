import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class instantiates a private list that will store the provided queries
 * given through the command line arguments, assuming that the input is valid.
 * It also stores a private map that stores the given queries matched with the
 * appropriate search result objects.
 */
public class QueryParser {
	
	private final Map<String, List<SearchResult>> results;
	private final List<String> lines;
	private final InvertedIndex invertedIndex;
	
	
	public QueryParser(InvertedIndex inputInvertedIndex)
	{
		results = new HashMap<String, List<SearchResult>>();
		lines = new ArrayList<String>();
		invertedIndex = inputInvertedIndex;
	}
	
	
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
	public void parseFile(String path, String outputPath) throws IOException
	{
				
		Path inputPath = Paths.get(path);
		
		try(BufferedReader bufferedReader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8))
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
	
	/**
	 * Reads in a line and adds them to the results map with 
	 * the appropriate search result and adds lines to lines map
	 *
	 * @param line
	 *            line to add to lines map and results map
	 * @throws IOException
	 * @return 
	 */
	public void parseLine(String line) throws IOException
	{
		List<SearchResult> partialSearch;
		
		String[] cleanedSplitLine = InvertedIndexBuilder.split(line);
		lines.add(line);
		partialSearch = invertedIndex.partialSearch(cleanedSplitLine);
		results.put(line, partialSearch);
	}
	
	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param outputPath
	 *            file to write search results to as JSON objects
	 * @throws IOException
	 * @return 
	 */ 
	public void writeToFile(String outputPath) throws IOException 
	{
		
		int i = 0;
		boolean lastLine = false;
		boolean firstLine = true;
		
		try(
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(outputPath.toString()), "UTF8"));
				)
		{
			for( String line: lines )
			{
				List<SearchResult> result = results.get(line);
				JSONWriter.resultsToJSON(result, outputPath, line, bufferedWriter, lastLine, firstLine);
				i++;
				firstLine = false;
				if ( i==lines.size()-1 )
				{
					lastLine = true;
				}
			}
		}

	}

}
