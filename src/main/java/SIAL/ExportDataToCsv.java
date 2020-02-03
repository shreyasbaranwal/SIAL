package SIAL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * 
 * @author davidtyrpak
 * A collection of static methods for writing hash structured data to CSV files.
 */
public class ExportDataToCsv {
	
	/**
	 * Writes HashMap data to CSV file. Expects only one value per key. In CSV file, each row is one Key/Value pair
	 * @param inputMap {@code <String, Integer>} as the input data structure. Expects only one value per key
	 * @param outputDir the directory where the CSV file will be produced
	 * @param outputFileName the name of the CSV file
	 * @param headers the column headers for the CSV file. Pick one for your Keys and one for your Values. 
	 * @throws IOException
	 * 
	 */
	
	public static void exportStringIntsMapToCsv(HashMap <String, Integer> inputMap, String outputDir, String outputFileName,
			String [] headers) throws IOException {
		
		//check if file exists to determine whether we should append or not.
				Boolean exists = Paths.get(outputDir, outputFileName).toFile().exists();
				
				//we will initialize these variables below, depending on if the CSV file already exists
				BufferedWriter writer;
				CSVPrinter csvPrinter;
				
				//if the file exits, we append to it without rewriting the headers as long as there are no lines in the file
				if (exists) {
					
					//we need this object so we can easily count the number of lines in the existing CSV file
					FileEditor linecounter = new FileEditor(new File(Paths.get(outputDir, outputFileName).toString()));
					
					//if the file exists and already has entries, append wihtout headers
					if (linecounter.countLines() > 0) {
					
				     writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName), StandardOpenOption.APPEND, 
				        StandardOpenOption.CREATE);
				 
				    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);}
					
					
					//if the file exists and doesn't have entries, add the headers and then append
					else {
						
						writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName), StandardOpenOption.APPEND, 
						        StandardOpenOption.CREATE);
						 
						    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
						    }
						
					}

				
				//Otherwise the file doesn't exist. So we write to a new file, with headers
				else
				{writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName));
				
				csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers)); }
				
				
				for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
					csvPrinter.printRecord(Arrays.asList(entry.getKey(), entry.getValue()));
				}
				csvPrinter.flush();
				csvPrinter.close();
			}
			
	
	/**
	 * Writes HashMap data to CSV file. Expects only one value per key. In CSV file, each row is one Key/Value pair
	 * @param map {@code <String, String>} as the input data structure. Expects only one value per key
	 * @param outputDir the directory where the CSV file will be produced
	 * @param outputFileName the name of the CSV file
	 * @param headers the column headers for the CSV file. Pick one for your Keys and one for your Values. 
	 * @throws IOException
	 * 
	 */
	public static void exportStringStringMapToCsv(HashMap<String, String> map, String outputDir, String outputFileName,
			String [] headers) throws IOException {
		
		//check if file exists to determine whether we should append or not.
		Boolean exists = Paths.get(outputDir, outputFileName).toFile().exists();
		
		//we will initialize these variables below, depending on if the CSV file already exists
		BufferedWriter writer;
		CSVPrinter csvPrinter;
		
		//if the file exits, we append to it without rewriting the headers as long as there are no lines in the file
		if (exists) {
			
			//we need this object so we can easily count the number of lines in the existing CSV file
			FileEditor linecounter = new FileEditor(new File(Paths.get(outputDir, outputFileName).toString()));
			
			//if the file exists and already has entries, append wihtout headers
			if (linecounter.countLines() > 0) {
			
		     writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName), StandardOpenOption.APPEND, 
		        StandardOpenOption.CREATE);
		 
		    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);}
			
			
			//if the file exists and doesn't have entries, add the headers and then append
			else {
				
				writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName), StandardOpenOption.APPEND, 
				        StandardOpenOption.CREATE);
				 
				    csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
				    }
				
			}

		
		//Otherwise the file doesn't exist. So we write to a new file, with headers
		else
		{writer = Files.newBufferedWriter(Paths.get(outputDir, outputFileName));
		
		csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers)); }
		
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			csvPrinter.printRecord(Arrays.asList(entry.getKey(), entry.getValue()));
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = new HashMap<String, Integer>();
	    map.put("Value1", 1);
	    map.put("Value2", 2);
	    map.put("Value3", 3);
	    map.put("Value4", 4);
	    map.put("Value5", 5);
	    
	    ExportDataToCsv.exportStringIntsMapToCsv(map, "/Users/davidtyrpak/Desktop", "exportdatatocsv.csv", new String [] {"Column1", "Column2"});

	    HashMap<String, String> mapb = new HashMap<String, String>();
	    mapb.put("Value1", "one");
	    mapb.put("Value2", "two");
	    mapb.put("Value3", "three");
	    mapb.put("Value4", "four");
	    mapb.put("Value5", "five");
	    
	    ExportDataToCsv.exportStringStringMapToCsv(mapb, "/Users/davidtyrpak/Desktop", "exportdatatocsvb.csv", new String [] {"Column1", "Column2"});
	    
	    
	}
	

}
