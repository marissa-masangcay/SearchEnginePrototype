import java.io.File;
import java.util.ArrayList;

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
	 * @see #fileParser.readFile(String file)
	 */
	public static void traverseDir(File directory){
		
		//Executed if directory is a directory
		if(directory.isDirectory())
		{

			File [] contents = directory.listFiles(); 

			for(int i=0;i<contents.length;i++){
				traverseDir(contents[i]); 
			}
		}

		//Executed when directory is a file
		else 
		{
			String fileName = directory.getName().toLowerCase(); 
			String path = directory.getPath();

			if(fileName.endsWith("txt")){
				fileParser.readFile(path);
			}
		}
	}
}
