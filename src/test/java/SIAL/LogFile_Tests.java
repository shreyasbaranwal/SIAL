package SIAL;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LogFile_Tests {
	
	@Rule
    public TemporaryFolder input_folder = new TemporaryFolder();
	
	@Rule
    public TemporaryFolder output_folder = new TemporaryFolder();
	
	
	@Test
	public void correctExtension() throws IOException {
		
		File log = output_folder.newFile("myLogFile.txt");
		 
		 String extension = "tiff";
		  
		 LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		 
		 assertTrue(exLogFile.getExtension() == extension);
	}
	
	
	//Ensure that metadata and file names are correctly written to log file
	  @Test 
	  public void fileNames_addedAfter_MetaDataLines() throws IOException {
	  
	  
	  File fileOne = input_folder.newFile("1.czi");
	  
	  File fileTwo = input_folder.newFile("2.czi");
	  
	  File fileThree = input_folder.newFile("3.czi");
	  
	  File log = output_folder.newFile("myLogFile.txt");
	  
	  String extension = "czi";
	  
	  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
	  
	  //write metadata 
	  exLogFile.writeMetadata("input_directory:" + exLogFile.getInputDirectory().toString());
	  
	  exLogFile.writeMetadata("output_directory:" + exLogFile.whichFile().getParent());
	  
	  exLogFile.writeMetadata("extension:" + exLogFile.getExtension());
	  
	  //collect final line of Meta_data and ensure it matches with what we added. This should be TRUE
	  boolean lastMetaDataBoolean = exLogFile.readFinalLine().matches("^Meta_data:extension:czi$");
	  
	  //write file names (pretending we analyzed these files)
	  exLogFile.appendLine(fileOne.getName());
	  
	  exLogFile.appendLine(fileTwo.getName());
	  
	  exLogFile.appendLine(fileThree.getName());
	  
	  //Ensure the last file name matches with the last file name we added. This should be TRUE
	  boolean lastFileBoolean = exLogFile.readFinalLine().matches("^3.czi$");
	  
	  assertTrue(lastFileBoolean == lastMetaDataBoolean); }
	 
	
}
	


