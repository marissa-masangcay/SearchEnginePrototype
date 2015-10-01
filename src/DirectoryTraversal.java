import java.io.File;
import java.util.ArrayList;

public class DirectoryTraversal {
	
	private static FileParser fileParser;

	/**
	 * Initializes an empty directory array list. 
	 * @return 
	 */
	public DirectoryTraversal() {
		
		fileParser = new FileParser();
	}
	
	public static void traverseDir(File dir){

		//Executed if dir is a directory
		if(dir.isDirectory()){

			File [] contents = dir.listFiles(); 

			for(int i=0;i<contents.length;i++){
				traverseDir(contents[i]); 
			}

		}

		//Executed when dir is a file
		else {
			String fileName = dir.getName().toLowerCase(); 
			String path = dir.getPath();

			if(fileName.endsWith("txt")){
				fileParser.readFile(path);
			}

		}

	}
	

}
