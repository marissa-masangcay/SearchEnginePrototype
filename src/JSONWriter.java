import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import java.util.Map.Entry;

public class JSONWriter {


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
	 * Writes the elements as a JSON object with nested array values to the
	 * specified output path. The output is in a
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void toJSON(String output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, BufferedWriter bufferedWriter) throws UnsupportedEncodingException, FileNotFoundException, IOException{

		bufferedWriter.write("{");

		//if elements is not empty
		if ( !elements.isEmpty() )
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
				for (Entry<String, TreeSet<Integer>> secondEntry: entry.getValue().entrySet())
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
						if (positionCounter!=0 && positionCounter< secondEntry.getValue().size())
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
					if (entry.getValue().size()>1 && textCounter<entry.getValue().size())
					{
						bufferedWriter.write(",");
					}

				}
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(1));
				bufferedWriter.write("}");
				wordCounter++;
				if (elements.size()>1 && wordCounter<elements.size())
				{
					bufferedWriter.write(",");
				}

			}
			bufferedWriter.write(System.lineSeparator());
			bufferedWriter.write(indent(0));
		}

		bufferedWriter.write("}");
	}
	
	
	/**
	 * Writes the search result objects as a JSON object with list values to the
	 * specified output path. The output is in a
	 * "pretty" format with 2 spaces per indent level.
	 * 
	 * <pre>
	 * {
	 *   "Line"(Query): [
	 *     {
	 *       "where": "file name",
	 *       "count": frequency,
	 *       "index": initialPosition
	 *     },
	 *     {
	 *       "where": "file name",
	 *       "count": frequency,
	 *       "index": initialPosition
	 *     }
	 *   ]
	 * }
	 * </pre>
	 * 
	 * @param searchResults
	 *            List of search results to write to given
	 *            file outputFile
	 * @param outputFile
	 *            file to write to as a JSON object
	 * @param line
	 *            query words given for list of search results
	 * @param bufferedWriter
	 *            BufferedWriter to write to file given
	 * @param lastLine
	 *            line that determines when to exclude ","
	 * @param firstLine
	 *            line that determines when to include "{"
	 * @throws IOException  
	 */
	public static void resultsToJSON(List<SearchResult> searchResults, String outputFile, String line, 
			BufferedWriter bufferedWriter, boolean lastLine, boolean firstLine) throws IOException
	{

		if ( firstLine )
		{
			bufferedWriter.write("{");
		}

		bufferedWriter.write(System.lineSeparator());
		bufferedWriter.write(indent(1));
		bufferedWriter.write(quote(line));

		bufferedWriter.write(":");
		bufferedWriter.write(" ");
		bufferedWriter.write("[");

		if ( !searchResults.isEmpty() )
		{
			for ( int i = 0; i < searchResults.size(); i++ )
			{
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(2));
				bufferedWriter.write("{");

				//writes file name
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(3));
				bufferedWriter.write(quote("where"));
				bufferedWriter.write(": ");
				bufferedWriter.write(quote(searchResults.get(i).getFileName()));
				bufferedWriter.write(",");

				//writes count/frequency
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(3));
				bufferedWriter.write(quote("count"));
				bufferedWriter.write(": ");
				bufferedWriter.write(String.valueOf(searchResults.get(i).getFrequency()));
				bufferedWriter.write(",");

				//writes index/initial position
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(3));
				bufferedWriter.write(quote("index"));
				bufferedWriter.write(": ");
				bufferedWriter.write(String.valueOf(searchResults.get(i).getInitialPosition()));

				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(2));
				bufferedWriter.write("}");

				if (i != searchResults.size()-1 )
				{
					bufferedWriter.write(",");
				}

				if ( i == searchResults.size()-1 )
				{
					bufferedWriter.write(System.lineSeparator());
					bufferedWriter.write(indent(1));
					bufferedWriter.write("]");
					if ( !lastLine )
					{
						bufferedWriter.write(",");
					}
				}
			}
		}
		if ( searchResults.isEmpty() )
		{
			bufferedWriter.write(System.lineSeparator());
			bufferedWriter.write(indent(1));
			bufferedWriter.write("]");
			if ( !lastLine )
			{
				bufferedWriter.write(",");
			}
		}
		if ( lastLine )
		{
			bufferedWriter.write(System.lineSeparator());
			bufferedWriter.write(indent(0));
			bufferedWriter.write("}");
		}
	}
}
