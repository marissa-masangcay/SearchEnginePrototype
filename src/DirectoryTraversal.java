import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.text.html.HTMLDocument.Iterator;

public class DirectoryTraversal {

	/**
	 * This class provides the method to recursively traverse through the 
	 * directory that was input by the user. It finds and reads all
	 * of the text files located in the given directory.
	 */


	/** Instantiates file parser to be called by the method {@link #traverseDir(File directory)}. */
	private static FileParser fileParser;


	/**
	 * Initializes an empty file parser. 
	 * @return 
	 */
	public DirectoryTraversal() {
		fileParser = new FileParser();
	}


	/**
	 * Traverses the directory passed in from args to attempt to 
	 * find and extract text files from.
	 * Uses {@link #fileParser.readFile(String file)} to 
	 * read and add to the map each text file found.
	 *
	 * @param directory input directory from command line args
	 * @return 
	 * @throws IOException 
	 * @see #fileParser.readFile(String file)
	 */

	public static void traverseDir(Path directory) throws IOException{

		//Executed if directory is a directory	
		if(Files.isDirectory(directory))
		{
			try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory))
			{
				for(Path directoryPaths: directoryStream)
				{
					traverseDir(directoryPaths);
				}	
			}
		}

		else
		{
			String fileName = directory.toString().toLowerCase();

			if(fileName.endsWith("txt"))
			{
				fileParser.readFile(directory.toString());
			}
		}

	}
}
