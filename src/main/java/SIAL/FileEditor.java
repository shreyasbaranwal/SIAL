package SIAL;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  methods for retrieving information from text files (plain text, csv, tsv).
 * 
 * 
 * @author davidtyrpak
 *
 */

public class FileEditor {
	
	private File infile;
	
	//should this constructor have error handling if the infile doesnt exist?
	public FileEditor (File infile) {
		
		this.infile = infile;
	}
	
	
	public File whichFile() {
		
		
		return this.infile;
		
	}
	
	
	
	/**
	 * 
	 * 
	 * @return the number of lines in the text file
	 * @throws IOException if text file does not exist
	 */
	public Integer countLines() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(this.infile));
		Integer lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		return lines;
		
	}
	
	public void appendLine(String inputText) throws IOException{
	
	if ( this.countLines() == 0) { //if the file is empty, add to the top of the text file
	
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.infile, false));//Set false for non-append mode
	    
		 writer.write(inputText); 
		 writer.newLine(); //add new line
		 writer.close();
	}
	
	else {//if the file already has lines of text, let's append  text
		
   BufferedWriter writer = new BufferedWriter(new FileWriter(this.infile, true));//Set true for append mode
             
    
   writer.write(inputText);
   writer.newLine(); //Add new line
   writer.close();
	}
   
	}
	
	/**
	 * 
	 * 
	 * @return the final line of text in the file
	 * @throws IOException if text file does not exist
	 */
	public String readFinalLine() throws IOException{
		
		//if the file is empty, return null
		if ( this.countLines() == 0) {return null;}
		
		//otherwise return the final line of text in the file
		else {
		BufferedReader input = new BufferedReader(new FileReader(this.infile));
	    String last = null, line;

	    while ((line = input.readLine()) != null) { 
	        last = line;
	    }
	    
	    input.close();
	    return last;
		}
	    
		
	}
	
	

	public static void main(String[] args) throws IOException {
		// for simple debugging and testing
		

		FileEditor example = new FileEditor(new File("/Users/davidtyrpak/Desktop/log.txt"));
		
		example.whichFile();
		
		System.out.println(example.countLines());
		
		example.appendLine("This is a test");
		
		System.out.println(example.readFinalLine());
		
		System.out.println(example.countLines());
		
		example.appendLine("This is a new test");
		
		System.out.println(example.countLines());
		
		System.out.println(example.readFinalLine());
		
		}

	}


