package SIAL;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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
 * 
 * 
 */
	public LogFile(File infile, File inputDir, String fExtension) {
		
		super(infile);
		
		this.inputDir = inputDir;
		
		this.fExtension = fExtension;
		
		
	}
	
	
	public File whichLogFile() {
		
		System.out.println(this.infile.toString());
		
		return this.infile;
		
	}
	
	/**
	 * 
	 * 
	 * @return the files that have not been analyzed yet
	 * @throws IOException 
	 * 
	 */
	
	public File [] notAnalyzedYet() throws IOException {
		
		// extension relevant files
		File[] inputDirFiles = this.inputDir.listFiles((d, name) -> name.endsWith(this.fExtension));
		
		// if the log file is empty, just return all the extension relevant files
		if (this.countLines() == 0) {return inputDirFiles; }
		
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
	
	
	

	public static void main(String[] args) throws IOException {
		// main method for debugging and examples
		
		File inputdir = new File("/Users/davidtyrpak/Desktop/output");
		
		File log = new File("/Users/davidtyrpak/Desktop/output/log.txt");
		
		String extension = "czi";
		

		LogFile exLogfile  = new LogFile(log, inputdir, extension);
		
		System.out.println("All files");
		
		//lambda expression to only list files in input directory having specified extension
		for (File file : inputdir.listFiles((d, name) -> name.endsWith(extension))) {
			
			System.out.println(file.toString()); 
		}
			
		System.out.println("Files left to analyze");
		
		for (File fileb : exLogfile.notAnalyzedYet()) {
			
			System.out.println(fileb.toString());
			
			
		}
		
			
		
			
		}
		
		
			
	
		
		


		
		

	}


