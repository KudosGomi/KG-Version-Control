import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/*
 * 		args[0] = [command] // Create class for all commands....
 * 		args[1] = [fileSrc] or [DirectorySrc]
 * 		args[2] = [ProjectName]
 * 		args[3] = [BranchName] <optional>
 */

public class KGVersionControl {
	
	// Dont user global variables?....
	private static Preferences systemPreferences;
	private static String workSpaceDirectory;
	private static Scanner userInput;
	static String OSfileSeparator = File.separator;
//	static String args[]; // ....
	

	public static void main(String[] args) throws BackingStoreException {
	
		// Creates/Loads the program's preferences file and then clears it to remove workspace directory FOR TESTING....
//		 systemPreferences = Preferences.userNodeForPackage(KGVersionControl.class);
//		 systemPreferences.clear();
		
		// Quick and easy way to test out the program with hard coded argument user-inputs....
		args = new String[4];
		args[0] = "save";
		args[1] = "/Users/Kudos/Documents/eclipse-workspace/VersionControlProgram/src";
		args[2] = "GroceryList";
//		args[3] = "Master"; // If there is no branch, prompt user to save in Master branch, else enter the name of a branch....
		
		// Better to create a variable globally or pass it around through parameters & only initiate it in the main method?
		userInput = new Scanner(System.in); 
		
		System.out.println("\nRetrieving workspace directory....\n");
		systemPreferences = Preferences.userNodeForPackage(KGVersionControl.class); // Comment out to clear the program's preferences....
		workSpaceDirectory = systemPreferences.get("workspace", "");
		
		// If no workspace directory was found ask user to enter a new workspace directory....
		// Initially thought about using regex here....
		if(workSpaceDirectory.isEmpty())
		{			
			// May need a lot of work in creating classes to capture OS specifics later in development....
			// workSpaceDirectory = enforceValidWorkspaceDirectoryWithRegex();
			enforceValidWorkspaceDirectoryWithRegex();
		}
		
		System.out.print("Workspace Directory: \n" + workSpaceDirectory);
		
		// Commands....
		
		// TBD: CHECK TO SEE IF USER WANTS TO SAVE ONLY A CERTIAN FILE(S) OR AN ENTIRE DIRECTORY....
		// TBD: CHECK TO SEE WHCIH FILES WHERE MODIFIED AND ONLY SAVE THOSE?
		if(args[0].equalsIgnoreCase("save"))
		{
			// Create a new version every time the user wants to save?
			// A version will act as an object that stores a time stamp of when the user wants push and commit, 
			// (save a file) due to changes made to their source files, as well as store a file,
			// associated with the time stamp of when the commit was made by the user....
			Version version = new Version(workSpaceDirectory + args[2]);
			
			// Check to see if fileSrc or DirectorySrc is VALID....
			// Create a method to check for file/Dir source and called i the beginning of the program?
			Path path = Paths.get(args[1]); // Global (static) variable?
			if(Files.exists(path)) 
			{
				checkIfProjectExist(version, args);
			}  
			else // if fileSrc or DirectorySrc is INVALID....
			{
				// Incomplete?
				System.out.println("Source of file(s) or source of directory could not be retrieved...."
						+ "\nQuiting program, please check for any typos and try again....");
				System.exit(0);
			}
		}
		
		// Code snippet below may be referenced for later on in development to output and save version's to a file....
		
		// Write object to file
//		FileOutputStream fileOutput = new FileOutputStream("/Users/Kudos/Desktop/data2.ser");
//		ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
//		objectOutput.writeObject(version object);
//		objectOutput.close();
		
		// Read object from file 
//		FileInputStream fileInput = new FileInputStream("/Users/Kudos/Desktop/data2.ser");
//		ObjectInputStream objectInput = new ObjectInputStream(fileInput);
//		Version result = (Version) objectInput.readObject();
//		objectInput.close();
				
//		result.getTime();
		
	}
	
	// Prompts user to enter a workspace directory and validates whether path exists or not.... 
	private static String enforceValidWorkspaceDirectoryWithRegex() {

		String userHomePath = System.getProperty("user.home") + OSfileSeparator;
		System.out.println("Workspace was NOT dectected.");
		System.out.println("Please enter a valid directory path to save development artifacts and system preferences....");
		System.out.print("\nWorkspace: " + userHomePath);
		workSpaceDirectory = userHomePath + userInput.nextLine();
	
		// Validates if workspace path exits....
		Path path = Paths.get(workSpaceDirectory);
		if(Files.exists(path))
		{
			System.out.println("Directory path is valid....\nCreating new workspace....");
			systemPreferences.put("workspace", workSpaceDirectory); // Saves the workspace path to the program's preferences file....
		}
		else // if user's input workspace path is INVALID, prompt user to try again until Files.exists() validates path....
		{
			System.out.print("Invalid directory: " + workSpaceDirectory + "....\nCheck for any typos and please try again.\n"
					+ "\nWorkspace: " + userHomePath);
			workSpaceDirectory = userHomePath + userInput.nextLine();
			
			// Considering whether to make the path variable global and/or static....
			path = Paths.get(workSpaceDirectory);
			while(Files.exists(path) == false)
			{					
				System.out.println("Invalid directory: " + workSpaceDirectory + "....\nCheck for any typos and try again.");
				System.out.print("\nWorkspace: " + userHomePath);
				workSpaceDirectory = userHomePath + userInput.nextLine();
				path = Paths.get(workSpaceDirectory);
			}

			// Saves workspace path to the program's preferences file once Files.exist validates user's input workspace path....
			System.out.println("\nDirectory path  is valid....\nCreating new workspace....");
			systemPreferences.put("workspace", workSpaceDirectory);
		}
		return workSpaceDirectory;
	}
	
	// Method checks if a project exists due to user input parameters for args[2] == [project]....
	// Version is passed for the creation of new directories for branches, day version's, and time version folders....
	private static void checkIfProjectExist(Version version, String[] args) {
		
		// Create project path....
		String projectFolderPath = workSpaceDirectory + OSfileSeparator + args[2];
		Path path = Paths.get(projectFolderPath);
		
		// Check to see if project exists....
		if(Files.exists(path))
		{
			// If a branch was NOT passed....
			if(args[3] == null)
			{
				// Prompt user to save into the 'Master' branch
				System.out.print("\n\nSave to \'Master\' branch? (Y/N): ");
				String userAnswer = userInput.nextLine();
				
				// Catch exception for invalid user input here.... 
				if(userAnswer.equalsIgnoreCase("y") || userAnswer.equalsIgnoreCase("yes"))
				{
					// Saving to the 'Master' branch....
					// Check if 'Master' branch exists, else always create a 'Master' branch for each and every project....
					projectFolderPath = projectFolderPath + OSfileSeparator + "Master";
					path = Paths.get(projectFolderPath);
					
					// If the 'Master' branch exits....
					if(Files.exists(path))
					{
						checkAndSaveToBranch();
						// Check if a day's version exists, else always create a new version for the day....
					}
					else // Else if the 'Master' branch DOES NOT exist....
					{
						File masterBranch = new File(projectFolderPath);
						masterBranch.mkdir();
						checkAndSaveToBranch();
						
					}
				}
				// Else if user input is NO, they dont want to save it to the 'Master branch', prompt user to enter a branch name, 
				// and then have a method to check if that branch exists to save a version copy there....
				else if(userAnswer.equalsIgnoreCase("no") || userAnswer.equalsIgnoreCase("n"))		
				{
					
				}
			}
			else // if a branch was passed into args[3].... 
			{
				// Save a version to the specified branch....
			}
		}
		else // Else if project DOES NOT exist....
		{
			// User input (args[2]) project DOES NOT exist...
			// Let user know project does not exits
			// Ask to retry in case user entered a typo.... 
			// Else ask user if they would want to create a new project.... 
			// "Retry entering a project or create a new project? (R/C): "
				// If user wants to retry, loop until a valid project is detected by Files.exist()....
				// If user wants to create new project, ask to enter a project save
				// Prompt user before creating folder if the same they have entered is what they want....
			// If user does not want to create a new project & there is no project, system exit....
			System.out.println();
		}
		
	}
	
	// 
	private static void checkAndSaveToBranch(String projectFolderPath, Version version, String args[]) {
		
		Path path = Paths.get(projectFolderPath + OSfileSeparator + version.getDayCreated());
		if(Files.exists(path))
		{
			// Project's 'Master' branch Day Version exists....
			// Inside the day the user tends to save a time copy....
			// Will save a new time copy by the hour & minute for each day,
			// Saves a new time copy version every minute, 
			// Or whenever the user saves after each new changes are made and need to be committed....
			// If a time copy exists, it is due to the user wanting to re-save the copy again under
			// a minute, else a new time copy will be saved the exact hour & new minute the user saves again....
			String dayVersionTimeCopyPath = projectFolderPath + OSfileSeparator + version.getDayCreated() + OSfileSeparator + version.getTime();
			try {
				// Create file/dir to copy into the directory destination....
				File args1 = new File(args[1]);
				System.out.println("Copy file/directory from: " + args1.toPath());
				
				// Create a time stamp folder version for the destination directory....
				File timeCopyFolder = new File(dayVersionTimeCopyPath);
				System.out.println("Save file/directory to:   " + timeCopyFolder.toPath());
				
				System.out.println("\nFiles being copied to destination from source....");
				copyDirectory(args1, timeCopyFolder, version);
			} catch (IOException e) {
				System.out.println("SYSTEM ERROR...."
						+ "\nFILE/DIRECTORY SOURCE COULD NOT BE CREATED...."
						+ "\nOR TIME VERSION COULD NOT BE CREATED...."
						+ "\nFAILED TO COPY FILE/DIRECTORY...."
						+ "\n\nContact developer at ForeSeenRunner@gmail.com...."
						+ "\nTERMINATING PROGRAM....");
				System.exit(1);
			}
		}
		else // if the day's version does NOT exist, create a new version for the day....
		{
			try {
				// Create the day folder AND THEN create a time folder with all the file(s) saved into the time folder....
				File dayFolder = new File(projectFolderPath + OSfileSeparator + version.getDayCreated());
				dayFolder.mkdir();
				copyDirectory(new File(args[1]), new File(projectFolderPath + OSfileSeparator + version.getDayCreated() + OSfileSeparator + version.getTime()), version);
			} catch (IOException e) {
				System.out.println("SYSTEM ERROR...."
						+ "\nFAILED TO CREATE DAY VERSION...."
						+ "\nFAILED TO COPY FILE/DIRECTORY FROM SOURCE TO DESTINATION...."
						+ "\n\nContact developer at ForeSeenRunner@gmail.com...."
						+ "\nTERMINATING PROGRAM....");
				System.exit(1);
			}
		}
	}
	
	// Checks to see if source is a file or a directory  
	private static void copyDirectory(File sourceDir, File targetDir, Version version) throws IOException {
		// if source is a directory, loop until a time version folder exists 
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir, version);
		} else { // if source is a file, then path is at the destination path, at the end of the last time version folder....
			System.out.println("File: " + sourceDir);
			Files.copy(sourceDir.toPath(), targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	// Checks to see if time version folder exists, else create a new one and store version....
	private static void copyDirectoryRecursively(File source, File target, Version version) throws IOException {
		// If Destination time version folder does NOT EXIST,
		// create a time version and check to see if it is the end to the destination path.... 
		if (!target.exists()) {
			target.mkdir();
			for (String child : source.list()) {
				
				copyDirectory(new File(source, child), new File(target, child), version);
			}
		}
		else if(target.exists()) // else if time version folder exist, save files....
		{
				for (String child : source.list()) {
					
					copyDirectory(new File(source, child), new File(target, child), version);
				}
		}
	}
}