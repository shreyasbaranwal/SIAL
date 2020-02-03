package SIAL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ; //this is for debugging method
import ij.ImagePlus;
import ij.gui.WaitForUserDialog;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;

//modified from this example: https://forum.image.sc/t/getting-selected-roi-in-roi-manager/3672/14

import net.imagej.ImageJ;


@Plugin(type = Command.class, headless = true,
menuPath = "Plugins > SIAL > ROI Recorder")
public class ROIRecorder implements Command {
	
	@Parameter(label = "file extension (e.g. tiff, jpeg, czi)", persist = false)
	private String fExt;
	
	@Parameter(label="Select input directory", style="directory", persist = false)
	private File inputDir;
	
	@Parameter(label="Select an output directory", style="directory", persist = false)
	private File outputDir;
	
	@Parameter(label="Select a log file (will record which images you have already analyzed)", style="file", persist = false)
	private File logFile;
	
	

	
	@Override
	public void run() {	
		
		if (!logFile.exists()) {
			try {
				Files.createFile(logFile.toPath());
			} catch (IOException e) {
				
			}}	
		
	//Creating a LogFile object will allow us to easily modify and retrieve information from the chosen logfile.txt
	LogFile logfileObj = new LogFile(logFile, inputDir, fExt);
			
		
	//lambda expression to only list files in input directory having specified extension
	File[] inputFiles = inputDir.listFiles((d, name) -> name.endsWith(fExt));
	
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
		
		//call RoiManager before we can get an instance of it
		RoiManager rm = new RoiManager(); 
		
		//here you get your roi manager, I think you could use also RoiManager.getRoiManager();		
		RoiManager roiMng = RoiManager.getInstance();
		
		
		//prompt user to select ROIs
		WaitForUserDialog wd_roi= new WaitForUserDialog("USER ROI SELECTION","First add ROIs to RoiManager. Then press OK ");
		
		//display dialog box and message.
		wd_roi.show();
		
		
		if (roiMng.getCount() == 0) { //ensure user has selected ROIs before pressing OK on user dialog box
			
			
			System.out.println("You pressed OK without first selecting ROIs!"); 
			//I should add a pause function so that the user has time to see this error message
			IJ.log("You pressed OK without first selecting ROIs!"); 
			
			imp.close();
			
			roiMng.close();
			
			
			IJ.run("Close All", "");
			
			System.exit(1); //
			
			} 
		
		//measure ROIset, save ROIset and results, and close ROImanager;
		
		roiMng.runCommand(imp, "Select All");
		
		roiMng.runCommand(imp, "Combine"); //combine all of our ROIs into one summary ROI 
		
		roiMng.runCommand(imp, "add"); //add this summary ROI (important for taking the average of the background readings)
		
		roiMng.runCommand(imp, "Measure");
		
		roiMng.runCommand("Save", roiSetPath.toString());
  
		roiMng.close();
		
		
		//save Results table and then close it
		IJ.selectWindow("Results"); 
		IJ.saveAs("Results", resultsPath.toString());
		IJ.run("Close");
		imp.close();
		
		//assert that the number of files in output_dir is increased by 2 by the addition of the Roiset and Results table
		assert outputFiles.length == outputFiles_count + 2: "Output directory should have added roiset and results csv file for this image but didnt.";
		
		//if we pass the assert statement, update outputFiles_count
		outputFiles_count = outputFiles.length;
		
		//write filename to log file
		try {
			logfileObj.appendLine(imp.getTitle());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
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