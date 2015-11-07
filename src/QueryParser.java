import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	
	// TODO Always initialize instance members in the constructor
	// TODO final
	
	private Map<String, List<SearchResult>> results = new HashMap<String, List<SearchResult>>();
	private List<String> lines = new ArrayList<String>();
	
	// TODO Take InvertedIndex as a parameter to the constructor instead of to the parseFile() method.
	
	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param path
	 *            file to read in for queries
	 * @param invertedIndex
	 *            inverted index to build search result list
	 * @param outputPath
	 *            file to write search result objects to
	 * @return 
	 */
	public void parseFile(String path, InvertedIndex invertedIndex, String outputPath) throws IOException
	{
		// TODO Don't call new here
		List<SearchResult> partialSearch = new ArrayList<SearchResult>();
		
		// TODO Use Files.newBufferedReader(), Files.newBufferedWriter
		
		try(BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(path), "UTF8")))
		{
			// TODO Move this writer to the write method.
			try(
				
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
							(new FileOutputStream(outputPath.toString()), "UTF8"));
					)

			{
				String line;
				//Reads in each line of file
				while ( (line = bufferedReader.readLine()) != null ) 
				{
					// TODO Put these lines below into a method, public void parseLine(String line)
					//iterates through lines of queries from files
					String[] cleanedSplitLine = InvertedIndexBuilder.split(line);
					lines.add(line);
					partialSearch = invertedIndex.partialSearch(cleanedSplitLine);
					results.put(line, partialSearch);
				}
				
				// TODO This call should happen in Driver
				//writes search results to file 
				writeToFile(outputPath, bufferedWriter);
			}
		}
	} 
	
	/**
	 * Reads in a file to parse words/lines and add them to the lines list
	 * and adds them to the results map with the appropriate search result
	 * objects mapped to them. 
	 *
	 * @param outputPath
	 *            file to write search results to as JSON objects
	 * @param bufferedWriter
	 *            bufferedWriter to pass in to write to outputPath file
	 * @throws IOException
	 * @return 
	 */
	public void writeToFile(String outputPath, BufferedWriter bufferedWriter) throws IOException 
	{
		// TODO Create a buffered writer here, don't take one as a parameter
		
		int i = 0;
		boolean lastLine = false;
		boolean firstLine = true;
		
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
