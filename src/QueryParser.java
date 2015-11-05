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
 * This class instantiates a private map that will store the user input
 * given through the command line arguments, assuming that the input is valid.
 */
public class QueryParser {
	
	private Map<String, List<SearchResult>> results = new HashMap<String, List<SearchResult>>();
	private List<String> lines = new ArrayList<String>();
	
	/**
	 * Reads in a file to parse words and add them at their positions found
	 * along with text file's name to the inverted index. 
	 *
	 * @param file
	 *            file to read in
	 * @return 
	 */
	public void parseFile(String path, InvertedIndex invertedIndex, String outputPath) throws IOException
	{

		List<SearchResult> partialSearch = new ArrayList<SearchResult>();
		try(BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(path), "UTF8")))
		{
			try(
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter
							(new FileOutputStream(outputPath.toString()), "UTF8"));
					)

			{
				String line;
				//Reads in each line of file
				while ((line = bufferedReader.readLine()) != null) 
				{
					//iterates through lines of queries from files
					String[] cleanedSplitLine = InvertedIndexBuilder.split(line);
					lines.add(line);
					partialSearch =invertedIndex.partialSearch(cleanedSplitLine);
					results.put(line, partialSearch);
				}
				writeToFile(outputPath, bufferedWriter);
			}
		}
	} 
	
	public void writeToFile(String outputPath, BufferedWriter bufferedWriter) throws IOException 
	{
		int i = 0;
		boolean lastLine = false;
		boolean firstLine = true;
		for(String line: lines)
		{
			List<SearchResult> result = results.get(line);
			JSONWriter.resultsToJSON(result, outputPath, line, bufferedWriter, lastLine, firstLine);
			i++;
			firstLine = false;
			if(i==lines.size()-1)
			{
				lastLine = true;
			}
		}
	}


}