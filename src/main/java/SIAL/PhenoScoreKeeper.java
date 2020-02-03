package SIAL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

/*Records user generated phenotype scores for images
 * 
 */


import org.junit.Assert;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.NumberWidget;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.WaitForUserDialog;
import ij.io.Opener;
import net.imagej.ImageJ;



@Plugin(type = Command.class, headless = true, menuPath = "Plugins > SIAL > PhenoScoreKeeper")
public class PhenoScoreKeeper implements Command {
	
	
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory where PhenotypeScores.csv file will be placed", style="directory", persist = false)
	private File outputDir;
	
	@Parameter(label="Select a log file (will record which images you have already scored)", style="file", persist = false)
	private File logFile;

	@Parameter(label = "Input number of phenotypes",
			style = NumberWidget.SPINNER_STYLE, min = "0", max = "1000")
		private Integer spinnerInteger;

	
	
	@Override
	public void run() {
		// check if log file exists, if not, create it
	
		if (!logFile.exists()) {
		try {
			Files.createFile(logFile.toPath());
		} catch (IOException e) {
			
		}}	
		
		//Creating a LogFile object will allow us to easily modify and retrieve information from the chosen logfile
		LogFile logfileObj = new LogFile(logFile, inputDir, fExt);

		
		//lambda expression to only list files having specified extension
		File[] inputFiles = inputDir.listFiles((d, name) -> name.endsWith(fExt));		
			
		// Store phenotype scores and original filenames in hashmap/dictionary
		HashMap<String, Integer> phenoDict = new HashMap <String, Integer>();	
		
		//to do for loop where we open each image, record phenotype, and write to log file and PhenoScore file.
		try {
			for (File file : logfileObj.notAnalyzedYet()) {
				
				ImagePlus imp = Opener.openUsingBioFormats(file.getAbsolutePath());
				
				Assert.assertNotNull("Image is null. Ensure Bioformats is installed and path is correct", imp);
				
				imp.show();
				
				String string_score;
				Integer score;
				
				//get user input score. 01/27/20 Not currently working.
				while (true) {
				 string_score  = IJ.getString("Enter your phenotype score for this ROI", "1" + "-" + Integer.toString(spinnerInteger));
				//convert that score to an integer
				 score = Integer.valueOf(string_score);
				
				//if user input is within the correct range, break
                 if (score <= spinnerInteger | score >= 1) {
					break;
					
				}
                 // otherwise continue while loop until user inputs correct range
                 WaitForUserDialog wd_roi= new WaitForUserDialog("Incorrect Phenotype Range","Phenotype score must be between your specified range: " + "1" + "-" + Integer.toString(spinnerInteger) +
                		 System.lineSeparator() + "Press OK to renalyze last image");
         		
         		//display dialog box and message.
         		wd_roi.show();
				}
				
					
				//Place filename of current image into phenoDict
					String fileNameKey = FilenameUtils.getName(file.toString());							
					
					 phenoDict.put(fileNameKey, score);
					 
					//write filename to log file
						try {
							logfileObj.appendLine(imp.getTitle());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					imp.close();
					
				}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
			//write the HashMap of phenotype scores and original filenames to PhenoScore.txt
			try {
		
				ExportDataToCsv.exportStringIntsMapToCsv(phenoDict, outputDir.toString(), "PhenotypeScores.csv", 
						new String [] {"Image", "Phenotype"});
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
			
			
		
			
			
			
		}
	
	
	
	
	public static void main(String[] args) {
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// Launch the command right away.
		ij.command().run(PhenoScoreKeeper.class, true);

	}








	

}
