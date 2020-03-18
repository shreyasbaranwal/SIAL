package SIAL;

import java.io.File;
import java.io.FileNotFoundException;
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
	
	
	//recordsFile will record which files we've analyzed and will store our metadata for this experiment (e.g. inputDir, fExt, outputDir)
	//By default the recordsFile is prefixed with PhenoLog.
	//Also by default this PhenoLog file is always placed in the outputDir
	
	@Parameter(label = "If a continued analysis, load your PhenoLog file. If a new analysis, ignore this field and fill out ALL below fields", 
			style = "file", persist = false, required  = false)
	private File recordsFile;
	
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false, required = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false, required = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory", style="directory", persist = false, required = false)
	private File outputDir;
	

	@Parameter(label = "Input number of phenotypes",
			style = NumberWidget.SPINNER_STYLE, min = "0", max = "1000", persist = false, required = false)
		private Integer spinnerInteger;

	
	
	@Override
	public void run() {
		
		//Creating a LogFile object will allow us to easily modify and retrieve information from the chosen recordsFile. 
		//We will instantiate this object based on which one of the four below if statements gets executed
		LogFile logfileObj = null;
		
		//We will use this date variable to name files
		String date = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
		
		//1. Error. User must either create a new experiment or load a previous PhenoLog file, otherwise their progress will not be recorded.
		if (recordsFile == null && (fExt == null || inputDir == null || outputDir == null || spinnerInteger == null )) {
			ui.showDialog("WARNING! You didnt load a PhenoLog file or properly create a new experiment.");
			throw new IllegalArgumentException("WARNING! You didnt load a PhenoLog file or properly create a new experiment.");
		}
		
		//2. Error. User cannot load a PhenoLog file and then select any of the fields for a new analysis. This would mix up experiments
		if  ( recordsFile != null && (fExt != null || inputDir != null || outputDir != null || spinnerInteger != 0 )) {
			ui.showDialog("WARNING! You cannot load a previous PhenoLog file and fill out other fields.");
			throw new IllegalArgumentException("WARNING! You cannot load a previous PhenoLog file and fill out other fields.");
		}
		
		//3. New Analysis. This is OK. Create a new PhenoLog file in the chosen records directory, AS LONG AS THERE IS NO EXISTING PhenoLog FILE IN THIS DIRECTORY
		if  (recordsFile == null && (fExt != null && inputDir != null && outputDir != null && spinnerInteger != null )) {
			
			//First ensure that there are no existing PhenoLog files. We don't want to overwrite anything unintentionally!
			//This lambda expression will list all files starting with "PhenoLog"
			File[] phenoScoreKeeperFiles = outputDir.listFiles((d, name) -> name.startsWith("PhenoLog"));
			if (phenoScoreKeeperFiles.length != 0) {
				
				ui.showDialog("WARNING! There is already an existing PhenoLog in this directory."
						+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
				
				throw new IllegalArgumentException("WARNING! There is already an existing PhenoLog in this directory."
						+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
				
			}
			
			
			//If we pass the above if check, create the recordsFile and corresponding LofFile object
			//By default, the recordsFile is always created in the specified outputDir
			Path recordsFilePath = Paths.get(outputDir.getAbsolutePath(), "PhenoLog" + "_" + date + ".txt");
			
			File newRecordsFile = new File(recordsFilePath.toString());
			
			try {
				newRecordsFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logfileObj = new LogFile(newRecordsFile, inputDir, fExt);
			
			try {
				//record all relevant metadata to 
				logfileObj.writeMetaData("input_directory", inputDir.getAbsolutePath());
				logfileObj.writeMetaData("output_directory", logfileObj.whichFile().getParent());
				logfileObj.writeMetaData("file_extension", fExt);
				logfileObj.writeMetaData("number_of_phenotypes", spinnerInteger.toString());
				logfileObj.writeMetaData("date", date);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		//4. Continued Analysis. This also OK. Load the chosen PhenoLog records file and harvest the inputDir, outputDir, fExt, 
		//and number of phenotypes
		if  ( recordsFile != null && (fExt == null && inputDir == null && outputDir == null && spinnerInteger == 0 )) {
			
			
			/*
			 * create temporary log file so that we use the harvestMetaData() method to
			 * collect required metadata from the pre-existing PhenoLog file.
			 */
			LogFile tempLogFile = new LogFile(recordsFile, inputDir, fExt);
			
			try {
				String string_spinnerInteger = tempLogFile.harvestMetaData().get("number_of_phenotypes");
				int int_spinnerInteger = Integer.parseInt(string_spinnerInteger);
				spinnerInteger = Integer.valueOf(int_spinnerInteger);
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			
			//we will initialize input_directory in the below try/catch block
			File input_directory = null;
			
			try {
				input_directory = new File(tempLogFile.harvestMetaData().get("input_directory"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				ui.showDialog("WARNING! input directory not found in specified PhenoLog file.");
			}
			
			String file_extension = null;
			try {
				file_extension = tempLogFile.harvestMetaData().get("file_extension");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ui.showDialog("WARNING! file extension not found in specified PhenoLog file.");
			}
			
			logfileObj = new LogFile(recordsFile, input_directory, file_extension);
			
		}
		

		
		
			
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
