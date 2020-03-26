package SIAL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Log files record which files in a directory have already been analyzed
 * 
 * 
 * @author davidtyrpak
 *
 */

public class LogFile extends FileEditor {
	

/**
 * the file which will serve as your log file. It will record which inputDir files you have already analyzed.
 */
private File infile; 

/**
 * The directory that your log file is watching. We will analyze the files in this directory and write the file names to the log file after we complete the analysis for that file
 */
private File inputDir;

/**
 * only count files in the inputDir with this extension
 */
private String fExtension;


/**
 * Creates a new LogFileReader from given file, input directory, and file extension
 * @throws IOException 
 * 
 * 
 */
	public LogFile(File infile, File inputDir, String fExtension) {
		
		super(infile); //explicitly invoke the constructor for parent class FileEditor
		
		this.inputDir = inputDir; //new field
		
		this.fExtension = fExtension;//new field
		
		
	}
	
	/**
	 * Explicit method for appending metadata to log file, for example the input directory, date,
	 * experiment name, etc. All metadata lines are prefixed with "Meta_data:" to enable differentiation between metadata and which files
	 * have already been analyzed.
	 * Ensure your meta_key and meta_value strings do not contain a colon!
	 * @param metadata_text For example "Name: My simple image analysis experiment" + 
	 * @return void
	 * @throws IOException if the log file object doesn't exist on your file system
	 * 
	 */
	public void writeMetaData(String meta_key, String meta_value) throws IOException {
		this.appendLine("Meta_data:" + meta_key + ":" + meta_value);
	}
	
	
	public File getInputDirectory() {
		
		return this.inputDir;
		
	}
	
	public String getExtension() {
		return this.fExtension;
	}
	
	/**
	 * 
	 * Harvests Metadata from log files. Meta_datas line start with "Meta_data:" and all subsequent data 
	 * on that line is separated by a colon. 
	 * An example Metadata line -> "Meta_data:input_directory:/home/user/Desktop/myfolder"
	 * LogFile.harvestMetaData() would return "input_directory" as key and "/home/user/Desktop/myfolder" as value
	 * 
	 * @return hashmap containing Metadata keys and values
	 * @throws FileNotFoundException 
	 * 
	 * 
	 */
	
	public HashMap <String, String> harvestMetaData() throws FileNotFoundException{
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		//Regex captures lines beginning with "Meta_data".
		//The parentheses group everything between the colons on that line
		Pattern p = Pattern.compile("^Meta_data:(.+?):(.+?)$");
		

			
		BufferedReader input = new BufferedReader(new FileReader(this.whichFile()));
			
		String line;
		try {
			while ( (line = input.readLine()) != null) {
			
			  
			  
			  //see 
			  Matcher m = p.matcher(line);
				if(m.matches()) {
				
				map.put(m.group(1), m.group(2));
				
				}
					
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
 
	
	/**
	 * 
	 * 
	 * @return the files from inputDir that have not been analyzed yet. 
	 * @throws IOException 
	 * 
	 */
	
	public File [] notAnalyzedYet() throws IOException {
		
		// extension relevant files
		File[] inputDirFiles = this.inputDir.listFiles((d, name) -> name.endsWith(this.fExtension));
		
		// if the log file is empty, just return all the extension relevant files
		if (this.countLines() == 0) {return inputDirFiles; }
		
		//if the final line in the log file is Meta_data line, then we havent recorded/analyzed any files yet
		//The regular expression matches a line beginning with "Meta_data:" and then any number of optional characters, and then the end of the line.
		if (this.readFinalLine().matches("^Meta_data:(.*)?$")) {return inputDirFiles;}
		
		//otherwise return the files that are not in the log file
		else { 
		
		//this.readFinalLine() will return the last file name written to the log file
        Path lastFileName = Paths.get(this.inputDir.toString(), this.readFinalLine());
		
        //convert the path into a string and then get the corresponding file 
		File lastFile = new File(lastFileName.toString());
		
		//get the position of the file in inpurDir array
		Integer start = Arrays.asList(inputDirFiles).indexOf(lastFile);
		
		File [] filesToBeAnalyzed = Arrays.copyOfRange(inputDirFiles, start +1, inputDirFiles.length);
		
		return filesToBeAnalyzed;
		}
	}
	
	/**
	 * 
	 * 
	 * @return the number of meta data lines in the file. Meta data lines begin with "Meta_data:"
	 * @throws IOException if the log file doesn't exist
	 * 
	 */
	
	public Integer countMetaDataLines() throws IOException {

		BufferedReader input = new BufferedReader(new FileReader(this.whichFile()));
		Integer meta_lines = 0;
		
		String line;
	
		
		while ( (line = input.readLine()) != null) {
			
			//The regular expression matches a line beginning with "Meta_data:" and then any number of optional characters, and then the end of the line.
			if (line.matches("^Meta_data:(.*)?$")) {
				
				meta_lines++;
			}
		
		}
		
		 
		input.close();
		return meta_lines;

		
	}
	
	/**
	 * 
	 * 
	 * @return the number of files which have been analyzed. Each time a file is analyzed, its file
	 * name is written to the log file.
	 * @throws IOException if the log file doesn't exist
	 * 
	 */
	public Integer countAnalyzedFiles() throws IOException {
		
		//the number of analyzed files is just the total number of lines in the log file minus the number of metadata lines
		Integer analyzedFiles = this.countLines() - this.countMetaDataLines();
		
		return analyzedFiles;
		
		
		
	}
	
	
	

	public static void main(String[] args) throws IOException {
		// main method for debugging and examples
		LogFile exLogfile;
		
		File inputdir = new File("/Users/davidtyrpak/Desktop/output");
		
		File log = new File("/Users/davidtyrpak/Desktop/output/log.txt");
		
		String extension = "czi";
		

		 exLogfile  = new LogFile(log, inputdir, extension);
		 
		exLogfile.appendLine("1.czi");
		exLogfile.appendLine("2.czi");
		exLogfile.appendLine("3.czi");
		
		exLogfile.writeMetaData("input_directory", exLogfile.inputDir.toString());
		
		exLogfile.writeMetaData("output_directory", exLogfile.whichFile().getParent());
		
		exLogfile.writeMetaData("extension", exLogfile.getExtension());
		
		System.out.println(exLogfile.harvestMetaData());
		
	

			
		}
		
		
			
	
		
		


		
		

	}


