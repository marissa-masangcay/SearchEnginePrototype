import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.NavigableSet;


public class FileParser {
	
	
	private static TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	
	public FileParser()
	{
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}
	
	
	 /** Regular expression for removing special characters. */
    public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

    
    /** Regular expression for splitting text into words by whitespace. */
    public static final String SPLIT_REGEX = "(?U)\\p{Space}+";
    
    
    public static String clean(String text) {
        text = text.toLowerCase().trim();
        text = text.replaceAll(CLEAN_REGEX, "");
        return text;
    }
    
    
    public static String[] split(String text) {
    	String[] textSplit = new String[0];
        text = clean(text);
        if(!text.isEmpty())
        {
        	textSplit = text.split(SPLIT_REGEX);
        }
        return textSplit;
    }
    
    
    public static boolean add(String word, String text, int position)
    {
    	//Cleans word
    	String cleanedWord = clean(word);
    	
    	//if index doesn't contain word
    	if(!index.containsKey(cleanedWord))
    	{
    		index.put(cleanedWord, new TreeMap<String, TreeSet<Integer>>());
    		//if index doesn't contain text file name
    		if(!index.get(cleanedWord).containsKey(text))
    		{
    			index.get(cleanedWord).put(text, new TreeSet<Integer>());
    			//if index doesn't contain position number
    			if(!index.get(cleanedWord).get(text).contains(position))
    			{
    				index.get(cleanedWord).get(text).add(position);
    			}
    		}
    		//if index has text file name
    		else if(index.get(cleanedWord).containsKey(text))
    		{
    			//if index doesn't contain position for that text file name
    			if(!index.get(cleanedWord).get(text).contains(position))
    			{
    				index.get(cleanedWord).get(text).add(position);
    			}
    		}
    		return true;
    	}
    	
    	//if index contains word
    	else if(index.containsKey(cleanedWord))
    	{
    		//if index doesn't contain text file name
    		if(!index.get(cleanedWord).containsKey(text))
    		{
    			index.get(cleanedWord).put(text, new TreeSet<Integer>());
    			//if index doesn't contain position number within that text file
    			if(!index.get(cleanedWord).get(text).contains(position))
    			{
    				index.get(cleanedWord).get(text).add(position);
    			}
    		}
    		//if index contains text file name
    		else if(index.get(cleanedWord).containsKey(text))
    		{
    			//if index doesn't contain position number within that text file
    			if(!index.get(cleanedWord).get(text).contains(position))
    			{
    				index.get(cleanedWord).get(text).add(position);
    			}
    		}
    		
    	}
        return false;
    }
    
    
    
	public static void readFile(String file){
		int counter = 0;

		try {
			File inputFile = new File(file);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(file), "UTF8"));
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				
				String[] splitLine = split(line);
				
				for(int i = 0; i<splitLine.length; i++)
				{
					counter++;
					add(splitLine[i], inputFile.getName(), counter);
				}
			
			}
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public static void printIndex()
	{
		Object[] test = index.entrySet().toArray();
		
		for(int i = 0; i<test.length; i++)
		{
			System.out.println(test[i]);
		}
		writeNestedObject("/users/Marissa/Documents/CS212/untitled Folder/"
        		+ "cs212-mjmasangcay-project/index.json", index);

	}
	
	
	
	public static String indent(int times) throws IOException {
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}


	public static String quote(String text) {
		return "\"" + text + "\"";
	}
	
	
	private static boolean outputPair(Entry<String, Integer> entry, BufferedWriter bw, Boolean status) {
		try {
			bw.write(indent(1));
			bw.write(quote(entry.getKey()));
			bw.write(": ");
			bw.write(entry.getValue().toString());
		} catch (IOException e) {
			status = false;
			System.err.println("Could not print output pair");
		}
		return status;
	}
	
	public static boolean writeNestedObject(String output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		boolean status = true;
		FileWriter fileWriter;
		File inputFile = new File(output);

		try{

			fileWriter = new FileWriter(inputFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("{");

			//if elements is not empty
			if(!elements.isEmpty())
			{
				Entry<String, TreeMap<String, TreeSet<Integer>>> first = elements.firstEntry();
//				bufferedWriter.write(indent(1));
//				bufferedWriter.write(quote(first.getKey()));
//				bufferedWriter.write(":");
//				bufferedWriter.write(" ");
//				bufferedWriter.write("{");
				

				//if first element is not empty
				if(!first.getValue().isEmpty())
				{
					Entry<String, TreeSet<Integer>> firstEntry = first.getValue().firstEntry(); 
//					bufferedWriter.write(System.lineSeparator());
//					bufferedWriter.write(indent(2)+quote(firstEntry.getKey()));
//					bufferedWriter.write(":");
//					bufferedWriter.write(" ");
//					bufferedWriter.write("[");
//					bufferedWriter.write(System.lineSeparator());
//					bufferedWriter.write(indent(3)+firstEntry.getValue().first());
					for (Integer intEntry: firstEntry.getValue().tailSet(firstEntry.getValue().first(), false))
					{
//						bufferedWriter.write(",");
//						bufferedWriter.write("\n");
//						bufferedWriter.write(indent(3)+intEntry);
					}
					if(elements.firstEntry().getValue().size()>1)
					{
//						bufferedWriter.write("\n");
//						bufferedWriter.write(indent(1)+"]");
					}
				}


				
				for (Entry<String, TreeMap<String, TreeSet<Integer>>> entry: elements.tailMap(first.getKey(), true).entrySet())
				{
					bufferedWriter.write(System.lineSeparator());
					bufferedWriter.write(indent(1));
					bufferedWriter.write(quote(entry.getKey()));
					bufferedWriter.write(":");
					bufferedWriter.write(" ");
					bufferedWriter.write("{");
					
					//INSERT HERE
					
//					writeNestedObject2(output, entry.getValue().firstEntry().getValue());

					int counter1 = 0;
					for(Entry<String, TreeSet<Integer>> secondEntry: entry.getValue().entrySet())
					{
						bufferedWriter.write(System.lineSeparator());
						bufferedWriter.write(indent(2));
						bufferedWriter.write(quote(secondEntry.getKey()));
						bufferedWriter.write(":");
						bufferedWriter.write(" ");
						bufferedWriter.write("[");
						Integer firstInt = entry.getValue().firstEntry().getValue().first(); 
//						bufferedWriter.write(System.lineSeparator());
//						bufferedWriter.write(indent(3)+firstInt);
						int counter2 = 0;
						for(Integer thirdEntry: secondEntry.getValue().tailSet(entry.getValue().firstEntry().getValue().first(), true))
						{
							if(counter2!=0)
							{
								bufferedWriter.write(",");
							}
//							bufferedWriter.write(",");
	                		bufferedWriter.write(System.lineSeparator());
	                		bufferedWriter.write(indent(3)+thirdEntry);
	                		counter2++;
						}
						bufferedWriter.write(System.lineSeparator());
						bufferedWriter.write(indent(3));
						bufferedWriter.write("]");
						counter1++;
					}
					bufferedWriter.write(System.lineSeparator());
					bufferedWriter.write(indent(2));
					bufferedWriter.write("}");
				}
				bufferedWriter.write(System.lineSeparator());
				bufferedWriter.write(indent(1));
				bufferedWriter.write("]");
				bufferedWriter.write(System.lineSeparator());

			}
			bufferedWriter.write("}");
			bufferedWriter.close();	



		}catch (IOException e){
			status = false;
			System.err.println("Problem writing to the file: "+output);
		}

		return status;

	}
	
	public static boolean writeNestedObject2(String output, TreeMap<String, TreeSet<Integer>> elements) {
        boolean status = true;
        FileWriter fileWriter;
        
        try{
        	
            fileWriter = new FileWriter(output);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            bufferedWriter.write("{");
            bufferedWriter.write(System.lineSeparator());
            
            if(!elements.isEmpty())
            {
            	Entry<String, TreeSet<Integer>> first = elements.firstEntry();
            	bufferedWriter.write(indent(1));
    			bufferedWriter.write(quote(first.getKey()));
    	    	bufferedWriter.write(":");
    	    	bufferedWriter.write(" ");
    	    	bufferedWriter.write("[");
    	        
                if(!first.getValue().isEmpty())
                {
                	Integer firstInt = first.getValue().first(); 
                	bufferedWriter.write(System.lineSeparator());
                	bufferedWriter.write(indent(2)+firstInt);
                	for (Integer entry: first.getValue().tailSet(firstInt, false))
                    {
                		bufferedWriter.write(",");
                		bufferedWriter.write("\n");
                		bufferedWriter.write(indent(2)+entry);
                    }
                	if(elements.firstEntry().getValue().size()>1)
                	{
                		bufferedWriter.write("\n");
                		bufferedWriter.write(indent(1)+"]");
                	}
                }
                
            	
            	for (Entry<String, TreeSet<Integer>> entry: elements.tailMap(first.getKey(), false).entrySet())
                {
                	bufferedWriter.write(",");
                	bufferedWriter.write(System.lineSeparator());
                	bufferedWriter.write(indent(1));
        			bufferedWriter.write(quote(entry.getKey()));
        	    	bufferedWriter.write(":");
        	    	bufferedWriter.write(" ");
        	    	
        	    	bufferedWriter.write("[");
        	    	Integer secondInt = entry.getValue().first(); 
                	bufferedWriter.write(System.lineSeparator());
                	bufferedWriter.write(indent(2)+secondInt);
        	    	for (Integer entry2: entry.getValue().tailSet(entry.getValue().first(), false))
                    {
                		bufferedWriter.write(",");
                		bufferedWriter.write("\n");
                		bufferedWriter.write(indent(2)+entry2);
                    }
                }
            	bufferedWriter.write("\n");
            	bufferedWriter.write(indent(1));
            	bufferedWriter.write("]");
            	bufferedWriter.write(System.lineSeparator());

            }
            bufferedWriter.write("}");
            bufferedWriter.close();	
            
            
            
        }catch (IOException e){
        	status = false;
        	System.err.println("Problem writing to the file: "+output);
        }

        return status;
    }


}

