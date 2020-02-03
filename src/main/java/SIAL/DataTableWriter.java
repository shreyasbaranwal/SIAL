package SIAL;
//code inspired from: https://stackoverflow.com/questions/15413467/writing-from-hashmap-to-a-txt-file


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;



// c


/**
 * 
 * @author davidtyrpak
 * A collection of static methods for writing hash structured data to txt files.
 */
public class DataTableWriter {
	
	
	/**
	 * 
	 * @param inputMap
	 * @param outputDir
	 * @param outputFileName
	 * @throws IOException
	 * HashMapToTextStringInts expects HashMap {@code <String, Integer>} as the input data structure
	 */
	public static void HashMapToTextStringInts (HashMap <String, Integer> inputMap, String outputDir, String outputFileName) throws IOException {
		
		File file = new File(outputDir, outputFileName);
		
		
		
		//Writes HashMap to txt file
		
		 	FileWriter fstream;
		    BufferedWriter out;

		    // create your FileWriter and BufferedWriter. Note that when writing many small lines of text, BufferedWriter is preferred. Thats why it is used below
		    fstream = new FileWriter(file);
		    out = new BufferedWriter(fstream);

		    // initialize the line count
		    int count = 0;

		    // create your iterator for your map
		    Iterator<Entry<String, Integer>> it = inputMap.entrySet().iterator();

		    // then use the iterator to loop through the map, stopping when we reach the
		    // last record in the map or when we have printed enough records
		    while (it.hasNext() && count < inputMap.size() ) {

		        // the key/value pair is stored here in pairs
		        Entry<String, Integer> pairs = it.next();
		        System.out.println("Value is " + pairs.getValue());

		        //write key value pairs on each line to out
		        out.write(pairs.getKey() + "\t" + pairs.getValue() + "\n");

		        // increment the record count once we have printed to the file
		        count++;
		    }
		    // lastly, flush and close the file and end
		    out.flush();
		    out.close();
		}
	
	/**
	 * 
	 * @param inputMap
	 * @param outputDir
	 * @param outputFileName
	 * @throws IOException
	 * HashMapToTextStrings expects HashMap {@code <String, String>} as the input data structure. 
	 */
	public static void HashMapToTextStrings (HashMap <String, String> inputMap, String outputDir, String outputFileName) throws IOException {
		
		File file = new File(outputDir, outputFileName);
		
		System.out.println(file.toString());
		
		//Writes HashMap to txt file
		
		 	FileWriter fstream;
		    BufferedWriter out;

		    // create your FileWriter and BufferedWriter. Note that when writing many small lines of text, BufferedWriter is preferred. Thats why it is used below
		    fstream = new FileWriter(file);
		    out = new BufferedWriter(fstream);

		    // initialize the line count
		    int count = 0;

		    // create your iterator for your map
		    Iterator<Entry<String, String>> it = inputMap.entrySet().iterator();

		    // then use the iterator to loop through the map, stopping when we reach the
		    // last record in the map or when we have printed enough records
		    while (it.hasNext() && count < inputMap.size() ) {

		        // the key/value pair is stored here in pairs
		        Entry<String, String> pairs = it.next();
		        System.out.println("Value is " + pairs.getValue());

		        //write key value pairs on each line to out
		        out.write(pairs.getKey() + "\t" + pairs.getValue() + "\n");

		        // increment the record count once we have printed to the file
		        count++;
		    }
		    // lastly, flush and close the file and end
		    out.flush();
		    out.close();
		}
	
	
		
	
	
	
	
	//testing code
	public static void main(String[] args) {
		
		// create hashmap and use HashMapToText
		HashMap<String, Integer> map = new HashMap<String, Integer>();
	    map.put("Value1", 1);
	    map.put("Value2", 2);
	    map.put("Value3", 3);
	    map.put("Value4", 4);
	    map.put("Value5", 5);
	    
	    try {
			DataTableWriter.HashMapToTextStringInts(map, "/Users/davidtyrpak/Desktop", "test.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	}

}
