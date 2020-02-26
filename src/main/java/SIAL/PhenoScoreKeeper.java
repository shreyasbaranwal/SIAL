package SIAL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

/*Records user generated phenotype scores for images
 * 
 */


import org.junit.Assert;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.widget.NumberWidget;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.WaitForUserDialog;
import ij.io.Opener;
import net.imagej.ImageJ;



@Plugin(type = Command.class, headless = true, menuPath = "Plugins > SIAL > PhenoScoreKeeper")
public class PhenoScoreKeeper implements Command {
	
	@Parameter
	private UIService ui;
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory", style="directory", persist = false)
	private File outputDir;
	
	
	@Parameter(label="If a new analysis, select a directory for PhenoScoreKeeperLog file (this file records the images you have already analyzed)", style="directory", persist = false, required = false)
	private File recordsDirectory = null;
	
	@Parameter(label="If a continued anlaysis, load your PhenoScoreKeeperLog file.", style="file", persist = false, required = false)
			private File recordsFile = null;

	@Parameter(label = "Input number of phenotypes",
			style = NumberWidget.SPINNER_STYLE, min = "0", max = "1000")
		private Integer spinnerInteger;

	
	
	@Override
	public void run() {
		
		//Creating a LogFile object will allow us to easily modify and retrieve information from the chosen recordsFile. 
		//We will instantiate this object based on which one of the four below if statements gets executed
		LogFile logfileObj = null;
		
		//We will use this date variable to name files
		String date = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
		
		//1. Error. User must either create a new records file or load a previous version, otherwise their progress will not be recorded.
		if ( recordsDirectory == null && recordsFile == null) {
			ui.showDialog("WARNING! You didnt create a records file or load a previous records file!.");
			throw new IllegalArgumentException("You didnt create a records file or load a previous records file!.");
		}
		
		//2. Error. User cannot choose to create a new records file and load a previous version. This would mix up experiments
		if  ( recordsDirectory != null && recordsFile != null) {
			ui.showDialog("WARNING! You cannot create a new records file and load a previous records file.");
			throw new IllegalArgumentException("You cannot create a neww file and load a previous records file");
		}
		
		//3. New Analysis. This is OK. Create a new PhenoScoreKeeperLog file in the chosen records directory, AS LONG AS THERE IS NO EXISTING PhenoScoreKeeperLog FILE IN THIS DIRECTORY
		if  ( recordsDirectory != null && recordsFile == null) {
			
			//First ensure that there are no existing PhenoScoreKeeperLog files. We don't want to overwrite anything unintentionally!
			//This lambda expression will list all files starting with "PhenoScoreKeeperLog"
			File[] phenoScoreKeeperFiles = recordsDirectory.listFiles((d, name) -> name.startsWith("PhenoScoreKeeperLog"));
			if (phenoScoreKeeperFiles.length != 0) {
				
				ui.showDialog("WARNING! There is already an existing PhenoScoreKeeperLog in this directory."
						+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
				
				throw new IllegalArgumentException("WARNING! There is already an existing PhenoScoreKeeperLog in this directory."
						+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
				
			}
			
			
			
			
			Path recordsFilePath = Paths.get(recordsDirectory.getAbsolutePath(), "PhenoScoreKeeperLog" + "_" + date + ".txt");
			
			File newRecordsFile = new File(recordsFilePath.toString());
			
			try {
				newRecordsFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logfileObj = new LogFile(newRecordsFile, inputDir, fExt);
			
		}
		
		
		//4. Continued Analysis. This also OK. Load the chosen PhenoScoreKeeperLog records file
		if  ( recordsDirectory == null && recordsFile != null) {
			
			logfileObj = new LogFile(recordsFile, inputDir, fExt);
			
		}
		

		
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
