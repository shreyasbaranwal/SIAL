package SIAL;

import java.io.File;
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
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory", style="directory", persist = false)
	private File outputDir;
	
	@Parameter(label="If new analysis, select a directory for records file (records which images you have already analyzed)", style="directory", persist = false, required = false)
	private File recordsDirectory = null;
	
	@Parameter(label="If a continued anlaysis, select your records file.", style="file", persist = false, required = false)
			private File recordsFile = null;
	
	

	
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
				if ( recordsDirectory == null && recordsFile == null) {
					ui.showDialog("WARNING! You didnt create a records file or load a previous records file!.");
					throw new IllegalArgumentException("You didnt create a records file or load a previous records file!.");
				}
				
				//2. Error. User cannot choose to create a new records file and load a previous version. This would mix up experiments
				if  ( recordsDirectory != null && recordsFile != null) {
					ui.showDialog("WARNING! You cannot create a new records file and load a previous records file.");
					throw new IllegalArgumentException("You cannot create a neww file and load a previous records file");
				}
				
				//3. OK. Create a new records file in the chosen records directory
				if  ( recordsDirectory != null && recordsFile == null) {
					
					Path recordsFilePath = Paths.get(recordsDirectory.getAbsolutePath(), "ROI_Records_File" + "_" + date + ".txt");
					
					File newRecordsFile = new File(recordsFilePath.toString());
					
					try {
						newRecordsFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					logfileObj = new LogFile(newRecordsFile, inputDir, fExt);
					
				}
				
				
				//4. OK. load the chosen records file
				if  ( recordsDirectory == null && recordsFile != null) {
					
					logfileObj = new LogFile(recordsFile, inputDir, fExt);
					
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
    	throw new IOException("Your log file doesnt look correct." + System.lineSeparator() + "Because you produce one results file and ROIset for each image, "
    			+ "your output directory should have twice as many files as your log file has entries." + System.lineSeparator() + ""
    					+ "Be sure to check for hidden files in your output directory"); 
    } 
	catch (IOException e) {
        e.printStackTrace();
        IJ.log("WARNING! Your log file doesnt look correct." + System.lineSeparator() + "Because you produce one results file and ROIset for each image, "
    			+ "your output directory should have twice as many files as your log file has entries");
    
	 //should prompt user to re-enter log file with WaitForUserDialog() 
	} 
    
    
	
	//path for saving Roisets and measurements/results
	String outputdir_path = outputDir.getAbsolutePath();
	
				
	//set all the measurements that you will need. I just collect everything to be safe	
	IJ.run("Set Measurements...", 
			"area mean standard modal min centroid center perimeter shape feret's integrated median skewness area_fraction display redirect=None decimal=5");
	
	
	String prefix = "non_background";
	
	//logfileObj.notAnalyzedYet() returns the files in the input directory which have not been analyzed yet
	//try {
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
					
					else { //if there are ROIs for this channel, //select them, combine them, and add the combined ROI to the ROIManger
				
					rm.setSelectedIndexes(roiArr);
					
					rm.runCommand(imp, "Combine");
					
					rm.runCommand(imp, "Add");
					
					//Do everything possible to clear the selections in the ROI manager. So we can restart next channel with clean slate.
					
					rm.runCommand(imp, "Deselect");
					
					rm.runCommand(imp, "Select None");
					
					rm.run("Select None");
					
					}
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