package SIAL;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileEditor_Tests {
	
	@Rule
    public TemporaryFolder input_folder = new TemporaryFolder();
	
	
	//Ensure FileEditor object references the correct file on your system
	@Test
	public void correctWhichFile() throws IOException {
		
		File test_file = input_folder.newFile("myFile.txt");
		  
		 FileEditor exFileEditor = new FileEditor(test_file);
		 
		 assertTrue( exFileEditor.whichFile() == test_file);
	}
	
	// Ensure appendLine() method works correctly. Indirectly check that countLines() also works correctly
	@Test
	public void correctAppendLine() throws IOException {
		
		File test_file = input_folder.newFile("myFile.txt");
		  
		FileEditor exFileEditor = new FileEditor(test_file);
		
		assertTrue(exFileEditor.countLines() == 0);
		
		exFileEditor.appendLine("Line 1");
		
		assertTrue(exFileEditor.countLines() == 1);
		
		exFileEditor.appendLine("Line 2");
		
		assertTrue(exFileEditor.countLines() == 2);
		
		exFileEditor.appendLine("Line3");
		
		assertTrue(exFileEditor.countLines() == 3);
		
		exFileEditor.appendLine("Line4");
		
		assertTrue(exFileEditor.countLines() == 4);

		
	}
	
	@Test
	public void correctAppendLine_withBlankLines() throws IOException {
		
		File test_file = input_folder.newFile("myFile.txt");
		  
		FileEditor exFileEditor = new FileEditor(test_file);
		
		assertTrue(exFileEditor.countLines() == 0);
		
		//empty line
		exFileEditor.appendLine("");
		
		assertTrue(exFileEditor.countLines() == 1);
		
		//line with 1 space
		exFileEditor.appendLine(" ");
		
		assertTrue(exFileEditor.countLines() == 2);
		
		//line with 1 tab
		exFileEditor.appendLine("	");
		
		assertTrue(exFileEditor.countLines() == 3);
		
		//line with 1 tab and 1 space
		exFileEditor.appendLine(" 	");
		
		assertTrue(exFileEditor.countLines() == 4);
		
		//line with blank space and text
		exFileEditor.appendLine(" text");
		
		assertTrue(exFileEditor.countLines() == 5);

	}
	
	//Ensure empty file is correctly identified as having a final line == null
	@Test
	public void correctReadFinalLine_EmptyFile() throws IOException {
		File test_file = input_folder.newFile("myFile.txt");
		  
		FileEditor exFileEditor = new FileEditor(test_file);
		
		//empty file
		assertTrue(exFileEditor.readFinalLine() == null);
		
	}

	//Ensure final line of non-empty file is correctly read
		@Test
		public void correctReadFinalLine() throws IOException {
			File test_file = input_folder.newFile("myFile.txt");
			  
			FileEditor exFileEditor = new FileEditor(test_file);
			exFileEditor.appendLine("Line 1");
			
			
			
			exFileEditor.appendLine("Line 2");
			
			
			
			exFileEditor.appendLine("Line3");
			
			
			
			exFileEditor.appendLine("Line4");
			
			
			assertTrue(exFileEditor.readFinalLine().equals("Line4"));
		}
		
		
//Ensure final line of non-empty file is correctly read, even when there are blank lines
	@Test
	public void correctReadFinalLine_withBlankLines() throws IOException {
		File test_file = input_folder.newFile("myFile.txt");
		  
		FileEditor exFileEditor = new FileEditor(test_file);
		exFileEditor.appendLine("Line 1");
		
		
		
		exFileEditor.appendLine("Line 2");
		
		//empty line
		exFileEditor.appendLine("");
		
		//space 
		exFileEditor.appendLine(" ");
		
		//space plus tab
		exFileEditor.appendLine(" 	");
		
		
		exFileEditor.appendLine("Line3");
		
		//another empty line
		exFileEditor.appendLine("");
		
		
		exFileEditor.appendLine("Line4");
		
		
		assertTrue(exFileEditor.readFinalLine().equals("Line4"));
	}	

	
	
	
	
	
	

}
