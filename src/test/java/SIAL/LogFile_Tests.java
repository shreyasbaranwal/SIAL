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
	
	@Test
	public void correct_whichFile() throws IOException {
		File log = output_folder.newFile("myLogFile.txt");
		 
		 String extension = "tiff";
		  
		 LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		 
		 assertTrue(exLogFile.whichFile() == log) ;
		
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
	  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
	  
	  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
	  
	  exLogFile.writeMetaData("extension", exLogFile.getExtension());
	  
	  //collect final line of Meta_data and ensure it matches with what we added. This should be TRUE
	  boolean lastMetaDataBoolean = exLogFile.readFinalLine().matches("^Meta_data:extension:czi$");
	  
	  //write file names (pretending we analyzed these files)
	  exLogFile.appendLine(fileOne.getName());
	  
	  exLogFile.appendLine(fileTwo.getName());
	  
	  exLogFile.appendLine(fileThree.getName());
	  
	  //Ensure the last file name matches with the last file name we added. This should be TRUE
	  boolean lastFileBoolean = exLogFile.readFinalLine().matches("^3.czi$");
	  
	  assertTrue(lastFileBoolean == lastMetaDataBoolean); }
	 
	 
	//Ensure that the correct number of metadata lines are retrieved. 
	  //In this test the metadata is correctly written without any whitespace between the colon delimiters, and the metadata lines are added first
	@Test
	public void test_harvestMetaDataSize_noWhitespace_metaFirst() throws IOException {
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		  //write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  		  
		  //write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		  HashMap<String, String> metaDataMap = exLogFile.harvestMetaData();
		  		  
		  assertTrue(metaDataMap.size() == 3);
		  
		  
		
		 }
	
	    // Ensure that the correct number of metadata lines are retrieved, even when metadata lines are after file names. 
	  //In this test the metadata is correctly written without any whitespace between the colon delimiters, and the metadata lines are added FIRST
	@Test
	public void test_harvestMetaDataSize_noWhitespace_metaLast() throws IOException {
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		  //write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		  
		  HashMap<String, String> metaDataMap = exLogFile.harvestMetaData();
		  
		  assertTrue(metaDataMap.size() == 3);
		
		 }
	
    // Ensure that the correct number of metadata lines are retrieved, even when there are redundant metadata lines \	  //In this test the metadata is correctly written without any whitespace between the colon delimiters, and the metadata lines are added FIRST
	@Test
	public void test_harvestMetaDataSize_redundantMetaData() throws IOException {
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		  //write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		//write metadata again
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		  
		  HashMap<String, String> metaDataMap = exLogFile.harvestMetaData();
		  
		  assertTrue(metaDataMap.size() == 3); //we should still have only three unique metadata lines
		
		 }
	
	//Ensure that the correct number of metadata lines are retrieved
	@Test
	public void test_countMetaDataLines() throws IOException {
		
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		//write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		  
		  
		  //assert that we only have three Meta data lines in the log file
		  assertTrue(exLogFile.countMetaDataLines() == 3);
		  
		
	}
	
	//Ensure meta data lines are correctly measured, even when file names are written before meta data
	@Test
	public void test_reverse_countMetaDataLines() throws IOException {
		
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		//write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		  
		  
		  //assert that we only have three Meta data lines in the log file
		  assertTrue(exLogFile.countMetaDataLines() == 3);
		  
		
	}
	
	
	//Ensure that 0 is returned when no meta data lines have been written
	@Test
	public void test_zero_countMetaDataLines() throws IOException {
		
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		  
		  
		  
		  //assert that we have zero Meta data lines in the log file
		  assertTrue(exLogFile.countMetaDataLines() == 0);
		  
		
	}
	
	
	
	//Ensure that 0 is returned when the file is empty
	@Test
	public void test_empty_countMetaDataLines() throws IOException {
		
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  

		  
		  //assert that we have zero Meta data lines in the log file
		  assertTrue(exLogFile.countMetaDataLines() == 0);	  
		
	}
	
	
	//Ensure that the analyzed files count is correct
		@Test
		public void test_countAnalyzedFiles() throws IOException {
			
			File fileOne = input_folder.newFile("1.czi");
			  
			  File fileTwo = input_folder.newFile("2.czi");
			  
			  File fileThree = input_folder.newFile("3.czi");
			  
			  File log = output_folder.newFile("myLogFile.txt");
			  
			  String extension = "czi";
			  
			  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
			  
			//write metadata 
			  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
			  
			  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
			  
			  exLogFile.writeMetaData("extension", exLogFile.getExtension());
			  
			//write file names (pretending we analyzed these files)
			  exLogFile.appendLine(fileOne.getName());
			  
			  exLogFile.appendLine(fileTwo.getName());
			  
			  exLogFile.appendLine(fileThree.getName());
			  
			  
			
			  //assert that we correctly counted the three analyzed file name lines (i.e. the three analyzed files)
			  assertTrue(exLogFile.countAnalyzedFiles() == 3);
			  
			
		}
		
		
//Ensure that the analyzed files count is correct when meta data lines and file names are written in reverse order
	@Test
	public void test_reverse_countAnalyzedFiles() throws IOException {
		
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  
		//write file names (pretending we analyzed these files)
		  exLogFile.appendLine(fileOne.getName());
		  
		  exLogFile.appendLine(fileTwo.getName());
		  
		  exLogFile.appendLine(fileThree.getName());
		  
		//write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());
		  
		  
		  //assert that we correctly counted the three analyzed file name lines (i.e. the three analyzed files)
		  assertTrue(exLogFile.countAnalyzedFiles() == 3);
		  
		
	}

	
	
	//Ensure that 0 is returned when the file is empty
	@Test
	public void test_empty_countAnalyzedFiles() throws IOException {
		
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  

		//assert that we have zero analyzed file name lines in the log file
		  assertTrue(exLogFile.countAnalyzedFiles() == 0);	  
	}
		
		  
	//Ensure that 0 is returned when no file names have been written
	@Test
	public void test_zero_countAnalyzedFiles() throws IOException {
		
		File fileOne = input_folder.newFile("1.czi");
		  
		  File fileTwo = input_folder.newFile("2.czi");
		  
		  File fileThree = input_folder.newFile("3.czi");
		  
		  File log = output_folder.newFile("myLogFile.txt");
		  
		  String extension = "czi";
		  
		  LogFile exLogFile = new LogFile(log, input_folder.getRoot(), extension);
		  		  
		  
		//write metadata 
		  exLogFile.writeMetaData("input_directory", exLogFile.getInputDirectory().toString());
		  
		  exLogFile.writeMetaData("output_directory", exLogFile.whichFile().getParent());
		  
		  exLogFile.writeMetaData("extension", exLogFile.getExtension());

		  
		  //assert that we have zero analyzed file name lines in the log file
		  assertTrue(exLogFile.countAnalyzedFiles() == 0);
		  
		
	}
	
	
}

	

	
	
	
	 
	


