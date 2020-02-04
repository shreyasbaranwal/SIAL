package SIAL;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExportDataToCsv_Tests {
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	
	//standard usage case. Expect no errors
	@Test
	public void StringInts_NoErrorstest() throws IOException {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("Value1", 1);
	    map.put("Value2", 2);
	    map.put("Value3", 3);
	    map.put("Value4", 4);
	    map.put("Value5", 5);
	    
	    
		
		 ExportDataToCsv.exportStringIntsMapToCsv(map, folder.getRoot().toString(), 
				 folder.newFile("ExDaCSV_StringInts_NoErrorsTest1.csv").getName(),
		 new String [] {"Column1", "Column2"});
		 
	    
	}



	
	 
	 //Purpose: Check that the CSV file matches with the HashMap
	  @Test 
	  public void StringInts_KeysMatchValues() throws IOException {
	  
		  HashMap<String, Integer> map = new HashMap<String, Integer>();
	  
	  map.put("Value1", 2); 
	  map.put("Value2", 2); 
	  map.put("Value3", 3);
	  map.put("Value4", 4);
	  map.put("Value5", 4);
	  
	  File outputfile = folder.newFile("ExDaCSV_StringInts_NoErrorsTest2.csv");
	  
	  ExportDataToCsv.exportStringIntsMapToCsv(map, folder.getRoot().toString(),
			  outputfile.getName(), new String [] {"Column1", "Column2"});
	  
	  
	  String line = "";
 
	  //Read the file from ExportDataToCsv.exportStringIntsMapToCsv()
	  BufferedReader reader = new BufferedReader(new FileReader(outputfile));
		
	  //read each line till the end
	  while (reader.readLine() != null) {
		
		line = reader.readLine();
	  
		//since CSV, split by comma
	  String[] entry = line.split(",");
	  
	  //get the key
	  String csv_key = entry[0];
	  
	  //get the value
	  String csv_value = entry[1];
	  
	 
	  //does the key from the CSV file retrieve the correct value from the HashMap?
	boolean equals = map.get(csv_key).equals(Integer.parseInt(csv_value));

	assertTrue(equals);
	  
	  }
	
	  reader.close();
		  
	  }
	  

    
}















