package SIAL;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;



@Plugin(type = Command.class, headless = true,
	menuPath = "Plugins > SIAL > File Randomizer")
public class Randomizer implements Command {

	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory\n(must be empty)", style="directory", persist = false)
	private File outputDir;
	
	
	
	
	
	

@Override
public void run() {
	
		
		//lambda expression to only list files having specified extension
		File[] inputFiles = inputDir.listFiles((d, name) -> name.endsWith(fExt));
		
		if (inputDir.exists() && inputDir.isDirectory() && inputFiles.length > 0) { //if condition isn't passed we will throw an exception. If it is passed, we will sequentially examine additional issues below
			  
			if (!outputDir.exists() ) {
				throw new IllegalArgumentException("Check that the output directory exists");
			}
			
			if (!outputDir.isDirectory() ) {
				throw new IllegalArgumentException("Output directory must be a directory");
			}
			
			//The specified output directory must empty. This prevents mixing up of randomized images
			File [] outputFiles = outputDir.listFiles((d, name) -> name.endsWith(fExt));
			
			if (outputFiles.length != 0 ) {
				throw new IllegalArgumentException("Output directory must be empty. Have you run this analysis before?\n"
						+ "Also check for hidden files.");
			}
			
			
			//copy input files to output directory
			else {
			for (int i = 0; i < inputFiles.length; i++) {
				
				try {
					FileUtils.copyFileToDirectory(inputFiles[i], outputDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//update outputFiles to assert that all the inputFiles have been copied over
			outputFiles = outputDir.listFiles((d, name) -> name.endsWith(fExt));
			
			assert outputFiles.length == inputFiles.length : "Input and Output directories have an unequal number of files.";
			
			//generate set of random numbers
			Set <Integer> randomInts  = new HashSet<Integer>();
			
			// initialize a Random object somewhere; you should only need one
			Random random = new Random();
			
			//generate as many unique 3-digit random integers as you have files
			while (randomInts.size() < inputFiles.length) {
			
			// generate a random integer from 0 to 899, then add 100 for range 100-999
			Integer rand = random.nextInt(900) + 100;
			
			//if the set doesn't contain the integer, add it
			if (!randomInts.contains(rand)) { randomInts.add(rand);}		
			
}
			
			//assert that you have generated as many unique random integers as you have files
			assert randomInts.size() == outputFiles.length : "File number doesnt match with number of random integers.";

			//Now rename each file in outputDir with a random integer. Store random integer and original filename in hashmap/dictionary
			HashMap<String, Integer> randomDict = new HashMap <String, Integer>();
			//Create iterator to go through randomInts set
			Iterator<Integer> iterateRand = randomInts.iterator();
			
			
			//to robustly store the file extension, I create this extension variable. When we randomly rename the files we  will use this extension variable
	        String extension = FilenameUtils.getExtension(outputFiles[1].getName()); //returns "txt", "zip", "tiff" etc
			
			for (File file : outputFiles) {
				
				String fileNameKey = FilenameUtils.getName(file.toString());			
				
				 Integer randIntValue = iterateRand.next();
				 
				 File intFileName = new File(outputDir, randIntValue.toString() + "." + extension);
				 
				 file.renameTo(intFileName);
				
				 randomDict.put(fileNameKey, randIntValue);
				
				
			}
			
			//write the HashMap of random integers and original filenames to Key.txt
			try {
				ExportDataToCsv.exportStringIntsMapToCsv(randomDict, outputDir.toString(), "Key.csv", new String[] {"original_name", "random_number"});;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			}
			
		}	
	
		
else {
throw new IllegalArgumentException("Check that input directory exists and that it contains files with the specified extension");
			   	}		
	

			}	
	


	public static void main(String[] args) {
		
		// Launch ImageJ as usual.
		

		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// Launch the command right away.
		ij.command().run(Randomizer.class, true);

	}

}