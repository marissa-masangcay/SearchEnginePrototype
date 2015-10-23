import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO Move class Javadoc just before the class definition.
// TODO Consider combining DirectoryTraverse and InvertedIndexBuilder.
/**
 * This class provides the method to recursively traverse through the 
 * directory that was input by the user. It finds and reads all
 * of the text files located in the given directory.
 */
public class DirectoryTraverser {

	// TODO Remove InvertedIndexBuilder from the parameters
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
	public static void traverse(Path directory, InvertedIndexBuilder invertedIndexBuilder, InvertedIndex invertedIndex) throws IOException{

		//Executed if directory is a directory	
		if(Files.isDirectory(directory))
		{
			try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory))
			{
				for(Path directoryPaths: directoryStream)
				{
					traverse(directoryPaths, invertedIndexBuilder, invertedIndex);
				}	
			}
		}
		//Executed if a file
		else
		{
			String fileName = directory.toString().toLowerCase();

			if(fileName.endsWith("txt"))
			{
				// TODO Use static access InvertedIndexBuilder.parseFile(...)
				//Reads file and adds words read to inverted index data structure
				invertedIndexBuilder.parseFile(directory.toString(), invertedIndex);
				
			}
		}

	}
}
