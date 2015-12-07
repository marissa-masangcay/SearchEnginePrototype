import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * This class instantiates a private LinkedHashMap that will store the provided queries
 * given through the command line arguments, assuming that the input is valid.
 * It also stores a private map that stores the given queries matched with the
 * appropriate search result objects.
 */
public class QueryParser extends AbstractQueryParser {
	
	/**Initializes a LinkedHashMap to store query lines and matching
	 * search results*/
	private final LinkedHashMap<String, List<SearchResult>> results;
	
	/**Initializes an inverted index*/
	private final InvertedIndex invertedIndex;
	

	/**Initializes a Query Parser object as well as an empty results map
	  and an inverted index*/
	public QueryParser(InvertedIndex inputInvertedIndex)
	{
		results = new LinkedHashMap<String, List<SearchResult>>();
		invertedIndex = inputInvertedIndex;
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
		try (	
				BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outputPath) , StandardCharsets.UTF_8);
				)
		{
			JSONWriter.resultsToJSON(bufferedWriter, results);
		}

	}

}
