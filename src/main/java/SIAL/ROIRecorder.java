package SIAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.IJ; //this is for debugging method
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;


//modified from this example: https://forum.image.sc/t/getting-selected-roi-in-roi-manager/3672/14

import net.imagej.ImageJ;


@Plugin(type = Command.class, headless = true,
menuPath = "Plugins > SIAL > ROI Recorder")
public class ROIRecorder implements Command {
		
	@Parameter
	private UIService ui;
	
	//recordsFile will record which files we've analyzed and will store our metadata for this experiment (e.g. inputDir, fExt, outputDir)
	//By default the recordsFile is prefixed with ROI_Records_File.
	//Also by default this ROI_Records_File file is always placed in the outputDir

	
	@Parameter(label="If a continued anlaysis, load your ROI_Records_File. If a new analysis, ignore this field and fill out ALL below fields", 
			style="file", persist = false, required = false)
	private File recordsFile;
	
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory", style="directory", persist = false)
	private File outputDir;
	
	

	
	/**
	 *
	 */
	@Override
	public void run() {	
		
		//Creating a LogFile object will allow us to easily modify and retrieve information from the chosen recordsFile. 
				//We will instantiate this object based on which one of the four below if statements gets executed
				LogFile logfileObj = null;
				
				//We will use this date variable to name files
				String date = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
				
				//1. Error. User must either create a new records file or load a previous version, otherwise their progress will not be recorded.
				if (recordsFile == null && (fExt == null || inputDir == null || outputDir == null)) {
					ui.showDialog("WARNING! You didnt create a records file or load a previous records file!.");
					throw new IllegalArgumentException("You didnt create a records file or load a previous records file!.");
				}
				
				//2. Error. User cannot choose to create a new records file and attempt to start a new experiment. This would mix up experiments
				if  ( recordsFile != null && (fExt != null || inputDir != null || outputDir != null)) {
					ui.showDialog("WARNING! You cannot load a previous records file and start a new experiment. If you want to continue"
							+ "a previous experiment, simply load the corresponding ROI_Records_File and leave the other fields blank)");
					throw new IllegalArgumentException("WARNING! You cannot load a previous records file and start a new experiment. If you want to continue"
							+ "a previous experiment, simply load the corresponding ROI_Records_File and leave the other fields blank)");
				}
				
				//3. New Analysis. This is OK. Create a new ROI_Records_File file in the chosen directory as long as there is no existing ROI_Records_File in the directory.
				//We don't want users unintentionally overwriting their records files.
				if  (recordsFile == null && (fExt != null && inputDir != null && outputDir != null)) {
					
					File[] ROI_Records_Files = outputDir.listFiles((d, name) -> name.startsWith("ROI_Records_File"));
					
					if (ROI_Records_Files.length != 0) {
						
						ui.showDialog("WARNING! There is already an existing ROI_Records_File in this directory."
								+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
						
						throw new IllegalArgumentException("WARNING! There is already an existing ROI_Records_File in this directory."
								+ " If you are certain you dont need this file, you can manually delete it from your directory before using this program.");
					}
					
					//If we pass the above if check, create the recordsFile and corresponding LogFile object
					//By default, the recordsFile is always created in the specified outputDir
					Path recordsFilePath = Paths.get(outputDir.getAbsolutePath(), "ROI_Records_File" + "_" + date + ".txt");
					
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
						logfileObj.writeMetaData("date", date);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
				//4. Continued Analysis. This also OK. Load the chosen ROI_Records_File file and harvest the inputDir, outputDir, fExt, 
				//and any other required metadata
				if  ( recordsFile != null && (fExt == null && inputDir == null && outputDir == null)) {
					

					/*
					 * create temporary log file so that we use the harvestMetaData() method to
					 * collect required metadata from the pre-existing ROI_Records_File.
					 */
					LogFile tempLogFile = new LogFile(recordsFile, inputDir, fExt);
					
					
					
					//we will initialize input_directory in the below try/catch block
					File input_directory = null;
					
					//harvest input directory
					try {
						input_directory = new File(tempLogFile.harvestMetaData().get("input_directory"));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						ui.showDialog("WARNING! input directory not found in specified ROI_Records_File.");
					}
					
					
					//harvest file_extension
					String file_extension = null;
					try {
						file_extension = tempLogFile.harvestMetaData().get("file_extension");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ui.showDialog("WARNING! file extension not found in specified ROI_Records_File.");
					}
					
					//now we can properly initialize the log file
					logfileObj = new LogFile(recordsFile, input_directory, file_extension);
					
					//but we also need to grab the output directory from the ROI_Records_File so we know where to write output
					try {
						outputDir = new File (tempLogFile.harvestMetaData().get("output_directory"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ui.showDialog("WARNING! output directory not found in specified ROI_Records_File.");
					}
					
					
				}
				
			
	
	//we will need to keep track of the number of ZIP and CSV files in the output directory
	File [] zip_outputFiles = outputDir.listFiles((d, name) -> name.endsWith("zip"));
	File [] csv_outputFiles = outputDir.listFiles((d, name) -> name.endsWith("csv"));
	File [] outputFiles = ArrayUtils.addAll(zip_outputFiles, csv_outputFiles);
	
    Integer outputFiles_count = outputFiles.length;
	
	
   //At any given time, the output directory should have exactly twice as many files as the log file has entries
   try
   {
    if (logfileObj.countLines() != outputFiles_count/2) 
    	throw new IOException("Your ROI_Records_File doesnt look correct." + System.lineSeparator() + "Because you produce one results file and ROIset for each image, "
    			+ "your output directory should have twice as many files as your ROI_Records_File has entries." + System.lineSeparator() + ""
    					+ "Be sure to check for hidden files in your output directory"); 
    } 
	catch (IOException e) {
        e.printStackTrace();
        IJ.log("WARNING! Your ROI_Records_File doesnt look correct." + System.lineSeparator() + "Because you produce one results file and ROIset for each image, "
    			+ "your output directory should have twice as many files as your ROI_Records_File has entries");
    
	 //should prompt user to re-enter log file with WaitForUserDialog() 
	} 
    
    
	
	//path for saving Roisets and measurements/results
	String outputdir_path = outputDir.getAbsolutePath();
	
				
	//set all the measurements that you will need. I just collect everything to be safe	
	IJ.run("Set Measurements...", 
			"area mean standard modal min centroid center perimeter shape feret's integrated median skewness area_fraction display redirect=None decimal=5");
	
	
	String prefix = "non_background";
	
		try {
			for (File file : logfileObj.notAnalyzedYet()) {
				
			
				
			ImagePlus imp = Opener.openUsingBioFormats(file.getAbsolutePath());
			
			Assert.assertNotNull("Image is null. Ensure Bioformats is installed and path is correct", imp);
				
			imp.show();
			
			//image title without extension. Will use to save RoiSets and Results table
			String title = imp.getShortTitle();	
			
			//create path for RoiSet
			Path roiSetPath = Paths.get(outputdir_path, prefix + "_" + title + "_RoiSet.zip");
			
			//create path for results table.  
			Path resultsPath = Paths.get(outputdir_path, prefix + "_" + title + "_" + "results.csv");
			
			//call RoiManager 
			RoiManager rm = new RoiManager(); 
				
			
			//prompt user to select ROIs
			WaitForUserDialog wd_roi= new WaitForUserDialog("USER ROI SELECTION","First add ROIs to RoiManager. Then press OK ");
			
			//display dialog box and message.
			wd_roi.show();
			
			
			if (rm.getCount() == 0) { //ensure user has selected ROIs before pressing OK on user dialog box
				
				
				System.out.println("You pressed OK without first selecting ROIs!"); 
				//I should add a pause function so that the user has time to see this error message
				IJ.log("You pressed OK without first selecting ROIs!"); 
				
				imp.close();
				
				rm.close();
				
				
				IJ.run("Close All", "");
				
				System.exit(1); //
				
				} 
			
			//Now we go through each channel of the Image, combine all ROIs from that channel, and add the Combined/Summary ROI to the ROI manager
			
			 Roi [] CompositeRois = new Roi [imp.getNChannels()]; //this array will contain the merged ROI for each channel i.e. size = getNChannels. May use this later on
				
			 Roi [] selected_rois  = rm.getRoisAsArray(); //this array stores the ROIs we have selected
			 
			 for (int channel = 1 ; channel <= imp.getNChannels(); channel++) {
					
					
					
					List<Integer> roiIndexes = new ArrayList<Integer>(); //this array will contain the ROI indexes for the given channel (overwritten for every new channel)
					  
					for (Roi roi : selected_rois) {
						
						
						if (roi.getZPosition() == channel) {   roiIndexes.add(rm.getRoiIndex(roi)); }
						
						
						
					}
					
					//Now convert the ArrayList of Integers to an int array. Need to do this before we use setSelectedIndexes method
					int[] roiArr = roiIndexes.stream().mapToInt(Integer::intValue).toArray();
					
					if (roiArr == null) { ; } //if there were no ROIs for this channel, pass
					
					else if (roiArr.length == 0) { ; } //similar check. if there were no ROIs for this channel, pass
					
					else { //if there are ROIs for this channel, we need to select them, combine them, and add the combined ROI to the ROIManger
				
					
						/*
						 * But before we combine ROIs, we need to check if there was only 1 ROI for this channel. If there was, we can't
						 * combine ROIs. But for consistency, we will re-add that ROI, and rename the single ROI to indicate
						 * that it is the Summary ROI for the channel
						 */					
						
					if (roiArr.length == 1) { 
					
				
					//Integer iChannel = channel; 
					//rm.rename(roiArr[0], "Channel" + "_" + iChannel.toString() + "_" + "Summary");
					
					
					rm.select(roiArr[0]);
						
					rm.runCommand(imp, "Add");
						
					//Rename this Summary ROI 
					Integer iChannel = channel; 
					rm.rename(rm.getCount() - 1, "Channel" + "_" + iChannel.toString() + "_" + "Summary");
						
					rm.runCommand(imp, "Deselect");
					
					rm.runCommand(imp, "Select None");
					
					rm.run("Select None");
					
					}
					
					//if there was more than one ROI for this channel, we can combine all the ROIs and add that combined ROI and rename that combined ROI
					else {
					
					rm.setSelectedIndexes(roiArr);
					
					rm.runCommand(imp, "Combine");
					
					rm.runCommand(imp, "Add");
					
					//Rename this combined ROI/Summary ROI something useful
					Integer iChannel = channel; 
					rm.rename(rm.getCount() - 1, "Channel" + "_" + iChannel.toString() + "_" + "Summary");
					
					
					
					
					//Now do everything possible to clear the selections in the ROI manager so we can begin the next channel with a clean slate.
					
					rm.runCommand(imp, "Deselect");
					
					rm.runCommand(imp, "Select None");
					
					rm.run("Select None");
					
					}
					} //end of else statement for this current channel of the current Image (imp)
				} //end of for loop over each channel of the current Image (imp)
			
			//Now that we've updated our RoiManager, we need to measure the ROIset, save the ROIset and results, and close the ROImanager;
			rm.runCommand(imp,"Select All");
					
			rm.runCommand(imp, "Measure");
			
			rm.runCommand("Save", roiSetPath.toString());
  
			rm.close();
			
			
			// And save Results table and then close it
			IJ.selectWindow("Results"); 
			IJ.saveAs("Results", resultsPath.toString());
			IJ.run("Close");
			imp.close();
			 
			
			//And assert that the number of files in output_dir is increased by 2 by the addition of the Roiset and Results table
			assert outputFiles.length == outputFiles_count + 2: "Output directory should have added roiset and results csv file for this image but didnt.";
			
			//And if we pass the assert statement, update outputFiles_count
			outputFiles_count = outputFiles.length;
			
			//We write filename to logfileObj
			
			
			try {
				logfileObj.appendLine(imp.getTitle());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
			
			 
}//end of for loop over fExt images
		
		
			//catch block for non-existent logFileObj
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	


//** DEBUGGING METHOD
public static void main(String[] args) {
	// set the plugins.dir property to make the plugin appear in the Plugins menu
	final ImageJ ij = new ImageJ();
	ij.launch(args);

	// Launch the command right away.
	ij.command().run(ROIRecorder.class, true);
	
}

}