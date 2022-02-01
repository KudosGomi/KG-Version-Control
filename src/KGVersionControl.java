import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.naming.ldap.SortControl;

/*
 * 		args[0] = [command]
 * 		args[1] = [fileSrc] or [DirectorySrc]
 * 		args[2] = [ProjectName]
 * 		args[3] = [BranchName] <optional>
  
  
  		Quick note ideas:
  		
  		* Go back & use the "~" home user shortcut 
		  security reason of using full absolute path
  		
		* Create a random seed in the beginning, whenever the user wants to execute a command, the commands have to match the instance the seed was created to execute their features.
		Supposedly, if you pass in the same parameters, all seeds created will return the same random output.
		So I can create a seed for each command I have, and all seeds have to match a super seed. 
		Would that how somehow create a security feature? 
		
		* Add password access for backup directory, project's, and even branches and perhaps a hierarchy of administrative access.
		
		* Choose not to be able to create backup workspace directory's for security reasons.
		* Program can only create project's and branches WITHIN the backup workspace directory
		* Program will ask for a password each time a project and branch is made....
		
		*  
 * 
 */

public class KGVersionControl {
	
	// Create only one scanner for user input (System.in)....
	static final Scanner userInput = new Scanner(System.in);
	static final String OSfileSeparator = File.separator;	

	public static void main(String[] args) throws BackingStoreException {
		
		// Quick and easy way to test out the program with hard coded argument user-inputs....
		args = args.clone();
		// args[0] = "save";
		// args[1] = "file/dir Src";
		// args[2] = "NewProject";
		// args[3] = "TestBranch";
		
		// Creates/Loads the program's preferences file and then clears it to remove workspace directory FOR TESTING....
		Preferences systemPreferences = Preferences.userNodeForPackage(KGVersionControl.class);
		// systemPreferences.clear();
		String workSpaceDirectory = systemPreferences.get("workspace", "");
		
		// Create the list of commands....
		ArrayList<String> commandWordsList = new ArrayList<String>();
		commandWordsList.add("copy");
		commandWordsList.add("merge");
		commandWordsList.add("~KGCom");
		commandWordsList.add("retrieve");
		commandWordsList.add("save");
		commandWordsList.add("view");
		commandWordsList.add("workspace");
		// commandWordsList.add("comment"); // Allows user to make comments to branches/projects?
		// commandWordsList.add("create");
		// commandWordsList.add("git");
		
		// Different conditions for args.length == 0, 1, & 2
		if(args.length == 0)
		{
			// Use string format and perhaps regex....
			System.out.println("\nRun the application with the following key words:"
					+ "\n\n\t[command]: [fileSrc/dirSrc], [projectName], or [branchName]\n"
					+ "\nThe KG Version Control Program only accepts 4 or less parameters"
					+ "\nat a time.\n"
					+ "\nFor a list of [command]s key in and enter: ~KGCom"
					+ "\n\n[DirSrc/FileSrc] is the file or folder to be copied"
					+ "\nand saved to the backup workspace directory."
					+ "\n\n[projectName] can either be an existing project or to"
					+ "\ncreate a new project with a unique name."
					+ "\n\n[branchName] can either be an existing branch or to"
					+ "\ncreate a new branch to experiment on a copy of the"
					+ "\n\'Master\' branch or to create features for the main"
					+ "\nproject before approving it to the main work within"
					+ "\nthe \'Master\' branch.\n");
			System.exit(0);
		}
		else if(args.length == 1 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = "
					+ "\n[project] = "
					+ "\n[branch] = ");
				
			System.out.println("\nInvalid [command] for the KG Version Control program...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		else if(args.length == 1 && commandWordsList.contains(args[0]))
		{
			// Ignore case for these commands?....
			if(args[0].equalsIgnoreCase("~KGCom"))
			{
				printOutDescriptionOfCommands();
				// terminate program here?....
			}
			else if(args[0].equalsIgnoreCase("view"))
			{
				displayAllProjectsAndBranches();
			}
			else if(args[0].equalsIgnoreCase("workspace"))
			{
				System.out.print("\nRetrieving workspace directory....\n");
				if(workSpaceDirectory.isEmpty())
				{			
					workSpaceDirectory = enforceValidWorkspaceDirectoryWithRegex(systemPreferences);
					System.out.println("New Backup Workspace Directory: \n\n\t" + workSpaceDirectory + "\n");
					System.exit(0);
				}
				changeWorkSpace(systemPreferences, workSpaceDirectory);
			}
			
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = "
					+ "\n[project] = "
					+ "\n[branch] = ");
				
			System.out.println("\nEnter the appropriate parameters following the command for \"" + args[0] + "\"...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		else if(args.length == 2 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = " + args[1]
					+ "\n[project] = "
					+ "\n[branch] = ");
			
			// No need to take care of further input....
			// Each command will have their own following parameters & exceptions....
			System.out.println("\nInvalid [command] for the KG Version Control program...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		// Checking parameters to print out values for project & branch....
		else if(args.length == 3 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = " + args[1]
					+ "\n[project] = " + args[2]
					+ "\n[branch] = ");
			
			System.out.println("\nInvalid [command] for the KG Version Control program...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		else if(args.length == 4 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = " + args[1]
					+ "\n[project] = " + args[2]
					+ "\n[branch] = " + args[3]);
			
			System.out.println("\nInvalid [command] for the KG Version Control program...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		else if(args.length > 4 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\nParameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[fileSrc/dirSrc] = " + args[1]
					+ "\n[project] = " + args[2]
					+ "\n[branch] = " + args[3]);
			
			System.out.println("\nInvalid [command] for the KG Version Control program...."
					+ "\nDo not load more than 4 parameters into the program...."
					+ "\nFor a list of [command]s key in and enter: ~KGCom\n");
			System.exit(0);
		}
		
		System.out.print("\nRetrieving workspace directory....\n");
		
		// Initially thought about using regex here....
		if(workSpaceDirectory.isEmpty())
		{			
			// May need classes to capture OS specifics later in development....
			workSpaceDirectory = enforceValidWorkspaceDirectoryWithRegex(systemPreferences);
//			System.out.println("\nNew Backup Workspace Directory: \n\n\t" + workSpaceDirectory + "\n");
//			System.exit(0);
		}
		
		System.out.println("Workspace Directory: \n\n\t" + workSpaceDirectory);
		
		// Commands....
		// Replace commands with a switch statement?....
		
		// Copy command....
		if(commandWordsList.contains(args[0]) && args[0].equalsIgnoreCase("copy")){}
		
		// Save Command....
		// CHECK TO SEE IF USER WANTS TO SAVE ONLY A CERTIAN FILE(S) OR AN ENTIRE DIRECTORY....
		// CHECK TO SEE WHCIH FILES WHERE MODIFIED AND ONLY SAVE THOSE?
		if(commandWordsList.contains(args[0]) && args[0].equalsIgnoreCase("save"))
		{
			// Check if a project name was not passed....
			if(args.length == 2)
			{
				System.out.println("\nMissing a [project] parameter!"
						+ "\nTo \'save\' a file or a directory, please provide a [project] name.");
				System.exit(0);
			}
			else if(args.length > 4)
			{
				System.out.println();
				System.exit(0);
			}
			
			File fileOrDirSrc = new File(args[1]);
			String isFileOrDir = fileOrDirSrc.isFile() ? "file" : "directory";
			
			if(!Files.exists(fileOrDirSrc.toPath())) 
			{
				System.out.println("\nSource of file or source of directory does NOT exist...."
							 	 + "\nQuiting program, please check for any typos and try again.\n");
				System.exit(0);
			}
			
			if(args.length == 3)
			{
				checkIfProjectExist(workSpaceDirectory, fileOrDirSrc, args[2], "", isFileOrDir);
			}
			else if(args.length == 4)
			{
				checkIfProjectExist(workSpaceDirectory, fileOrDirSrc, args[2], args[3], isFileOrDir);
			}
			// System.exit(0);
		}
		
		// Print out an error message for not recognizing a command....
		
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
	
	private static void printOutDescriptionOfCommands() {

		// Needs print formatting work....
		// Include that the program only takes in 4 parameters....
		System.out.println("\nCommand List: "
				+ "\n* Parameters placed inside [] brackets are mandatory parameters, while parameters placesd"
				+ "\ninside <> brackets are optional parameters."
				+ "\n"
				+ "\n~KGCom : Display the list of commands and their appropriate parameters."
				+ "\n"
				+ "\ncopy [project/file] <branch> : To copy a project's folder or a file within the project."
				+ "\n\tOr copy a file or directory from a branch within the project."
				+ "\n"
				+ "\nmerge [srcBranch] [destBranch] : overwrties all files and sub directories from the dest"
				+ "\n\tbranch and copies over any files and/or any sub directories to the dest"
				+ "\n\tbranch from the src branch."
				+ "\n"
				+ "\nretrieve [project] <branch> <file>: retrieves a file or directory that may no longer"
				+ "\n\texist in the workspace. Can retrieve a file or directories from a month ago, 3 months"
				+ "\n\tago, 6 months ago, or a from year ago. Retrieve a file, or a directory, from a the "
				+ "\n\tproject or a branch, and any associated sub directories. "
				+ "\n"
				+ "\nsave [fileSrc/dirSrc] [project] <branch> : copy fileSrc/dirSrc to an existing project"
				+ "\n\twithin the back up workspace. If no project exists, program can create a new project"
				+ "\n\twith the entered name. Pass in a branch to save a copy to a project or save to the"
				+ "\n\tproject's \'Master\' branch."
				+ "\n"
				+ "\nview <project> <branch> : view all projects. If a project's name is passed, view all"
				+ "\n\tthe branches of the project. If a project's name and a branch's name is passed, view all"
				+ "\n\tof the branch's files and sub directories."
				+ "\n"
				+ "\nworkspace : change the current back up workspace directory path.\n");
		System.exit(0);
	}
	
	private static void displayAllProjectsAndBranches() {

		System.exit(0);
	}
	
	private static void changeWorkSpace(Preferences systemPreferences, String workSpaceDirectory) {
		
		System.out.println("\nWorkspace Directory: \n\n\t" + workSpaceDirectory);
		System.out.print("\nChange backup workspace directory path? (Y / N): ");
		String changeWorkSpaceDir = userInput.nextLine();
		
		if(changeWorkSpaceDir.equalsIgnoreCase("y") || changeWorkSpaceDir.equalsIgnoreCase("yes"))
		{
			String userHomePath = System.getProperty("user.home") + OSfileSeparator;
			System.out.print("\nEnter new backup workspace directory: \n" + userHomePath);
			workSpaceDirectory = userInput.nextLine();
			
			Path newWorkSpaceDir = Paths.get(userHomePath + workSpaceDirectory);
			if(!Files.exists(newWorkSpaceDir))
			{
				System.out.print("\nNew backup workspace directory that was entered is INVALID."
						+ "\nMake sure directory exists.\n");
				System.exit(0);
			}
			
			System.out.print("\nUpdate backup workspace directory to: (Y / N): \n" + userHomePath + workSpaceDirectory + "\033[F\033[47C");
			changeWorkSpaceDir = userInput.nextLine();
			
			if(changeWorkSpaceDir.equalsIgnoreCase("y") || changeWorkSpaceDir.equalsIgnoreCase("yes"))
			{
				System.out.println("\n\nWorkspace directory updated to: \n\n\t" + userHomePath + workSpaceDirectory);
				systemPreferences.put("workspace", userHomePath + workSpaceDirectory);
			}
			else if(changeWorkSpaceDir.equalsIgnoreCase("n") || changeWorkSpaceDir.equalsIgnoreCase("no"))
			{
				System.out.print("\n\nRun the program again with a certain new backup workspace directory.\n");
				System.exit(0);
			}
		}
		else if(changeWorkSpaceDir.equalsIgnoreCase("n") || changeWorkSpaceDir.equalsIgnoreCase("no"))
		{
			System.out.print("\nNo changes were made to the backup workspace directory.\n");
			System.exit(0);
		}
		System.out.println();
		System.exit(0);
	}
	
	// Prompts user to enter a workspace directory and validates whether path exists or not.... 
	private static String enforceValidWorkspaceDirectoryWithRegex(Preferences systemPreferences) {

		//Scanner enforceNewWorkSpaceUserInput = new Scanner(System.in);
		String userHomePath = System.getProperty("user.home") + OSfileSeparator;
		System.out.println("Workspace was NOT dectected....");
		System.out.println("\nPlease enter a valid backup workspace directory path to save development "
				+ "\nartifacts and system preferences....");
		System.out.print("\nWorkspace: " + userHomePath);
		// String workSpaceDirectory = userHomePath + enforceNewWorkSpaceUserInput.nextLine();
		String workSpaceDirectory = userHomePath + userInput.nextLine();
	
		// Validates if workspace path exits....
		Path workSpacePath  = Paths.get(workSpaceDirectory);
		if(Files.exists(workSpacePath))
		{
			System.out.println("\nBackup workspace directory path is valid....\nSetting up new backup workspace directory....");
			systemPreferences.put("workspace", workSpaceDirectory); // Saves the workspace path to the program's preferences file....
		}
		else 
		{
			// if user's input workspace path is INVALID, prompt user to try again until Files.exists() validates path?....
			// Update and work on later....
			while(Files.exists(workSpacePath) == false)
			{					
				System.out.println("Invalid directory....\nCheck for any typos and try again.");
				System.out.print("\nWorkspace: " + userHomePath);
				// workSpaceDirectory = userHomePath + enforceNewWorkSpaceUserInput.nextLine();
				workSpaceDirectory = userHomePath + userInput.nextLine();
				workSpacePath = Paths.get(workSpaceDirectory);
			}
			
			// Saves workspace path to the program's preferences file once Files.exist validates user's input workspace path....
			System.out.println("\nBackup workspace directory path is valid....\nSetting up new backup workspace directory...");
			systemPreferences.put("workspace", workSpaceDirectory);
		}
		// enforceNewWorkSpaceUserInput.close();
		return workSpaceDirectory;
	}
	
	// Method checks if a project exists due to user input parameters for args[2] == [project]....
	private static void checkIfProjectExist(String workSpaceDirectory, File fileOrDirSrc, String projectName, String branchName, String isFileOrDir) {
		
		// Establish project path....
		String projectFolderPath = workSpaceDirectory + OSfileSeparator + projectName;
		Path path = Paths.get(projectFolderPath);

		String nameOfFileOrDir = fileOrDirSrc.toString().substring(fileOrDirSrc.toString().lastIndexOf("/") + 1, fileOrDirSrc.toString().length());
		System.out.println("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to workspace....");
		System.out.println("Retrieving project \"" + projectName + "\"....");

		// Check to see if project exists....
		if(Files.exists(path))
		{
			// If a branch was NOT passed....
			if(branchName.isEmpty())
			{
				System.out.print("\nSave to \'Master\' branch? (Y / N): ");
				String userMasterBranchAnswer = userInput.nextLine();

				if(userMasterBranchAnswer.equalsIgnoreCase("y") || userMasterBranchAnswer.equalsIgnoreCase("yes")) // WORK ON OTHER INVALID INPUT....
				{
					// Saving to the 'Master' branch....
					System.out.print("\nSaving files to \'Master\' branch....");

					// Check if 'Master' path folder exists, else always create a 'Master' branch for every project....
					String masterBranchPath = projectFolderPath + OSfileSeparator + "Master";
					path = Paths.get(masterBranchPath);

					// If the 'Master' directory exits....
					if(Files.exists(path))
					{
						checkAndSaveToBranch(fileOrDirSrc, masterBranchPath, isFileOrDir);
					}
					else // Else if the 'Master' branch DOES NOT exist....
					{
						File masterBranch = new File(projectFolderPath);
						masterBranch.mkdir();
						checkAndSaveToBranch(fileOrDirSrc, masterBranchPath, isFileOrDir);

					}
				}
				// Else if user input is NO, does NOT want to save it to the 'Master' branch.... 
				else if(userMasterBranchAnswer.equalsIgnoreCase("n") || userMasterBranchAnswer.equalsIgnoreCase("no")) // WORK ON OTHER INVALID INPUT....
				{
					// Prompt user which branch to save to inside the project....
					System.out.print("Project: " + projectFolderPath + "...."
							+ "\nWhich branch would you like to save too: \n" + projectFolderPath + OSfileSeparator);
					String newBranchPath = projectFolderPath + OSfileSeparator + userInput.nextLine();

					// Check to see if user input branch exists....
					path = Paths.get(newBranchPath);
					if(Files.exists(path))
					{
						checkAndSaveToBranch(fileOrDirSrc, newBranchPath, isFileOrDir);
					}
					else // if branch does NOT exist....
					{
						// Create a new branch based off user input....
						// Calculate [54C by getting the length of newBranchPath....
						System.out.printf("\nBranch does NOT exist...."
								+ "\nWould you like to create a new branch using: (Y / N): \n" + newBranchPath + "\033[F\033[54C");
						String userNewBranchAnswer = userInput.nextLine();
						
						// WORK ON OTHER INVALID INPUT....
						if(userNewBranchAnswer.equalsIgnoreCase("y") || userNewBranchAnswer.equalsIgnoreCase("yes"))
						{
							checkAndSaveToBranch(fileOrDirSrc, newBranchPath, isFileOrDir);
						}
						else if(userNewBranchAnswer.equalsIgnoreCase("n") || userNewBranchAnswer.equalsIgnoreCase("no"))
						{
							System.out.println("To create a new BRANCH, run the program again with a certain"
									+ "\nBRANCH name -- if the branch does not exist already.\n");
							System.exit(0);
						}
						System.out.println();
					}
				}
				else // Else if user enters anything else but no or yes....
				{

				}
			}
			else if(!branchName.isEmpty())
			{
				// Project exist and user passed in a branch name....
				// Check if user input branch exists....
				String branchPath = projectFolderPath + OSfileSeparator + branchName;
				path = Paths.get(branchPath);
				if(Files.exists(path))
				{     
					checkAndSaveToBranch(fileOrDirSrc, branchPath, isFileOrDir);
				}
				else if(!Files.exists(path))
				{
					System.out.printf("\nBranch does NOT exist...."
							// Calculate [54C by getting the length of newBranchPath....
							+ "\nWould you like to create a new branch using: (Y / N): \n" + branchPath + "\033[F\033[54C");
					String newBranchUserInputAnswer = userInput.nextLine();
					
					// WORK ON OTHER INVALID INPUT....
					if(newBranchUserInputAnswer.equalsIgnoreCase("y") || newBranchUserInputAnswer.equalsIgnoreCase("yes"))
					{
						File newBranch = new File(branchPath);
						newBranch.mkdir();
						checkAndSaveToBranch(fileOrDirSrc, branchPath, isFileOrDir);
					}
					else if(newBranchUserInputAnswer.equalsIgnoreCase("n") || newBranchUserInputAnswer.equalsIgnoreCase("no"))
					{
						System.out.println("To create a new BRANCH, run the program again with a certain"
								+ "\nBRANCH name -- if the BRANCH does NOT exist already!\n");
						System.exit(0);
					}
					System.out.println();
				}
			}
		}
		else // else if PROJECT does NOT exist....
		{
			// Fix character spaces when displaying path below....
			int characterSpaces = "Would you like to create a new project using: (Y / N)? \n".length() - projectFolderPath.length();
			// System.out.print("\nProject does NOT exist...."
			// + "\nWould you like to create a new project using: (Y / N): \n" + projectFolderPath + "\033[1A\033[16C");
			System.out.print("\nProject does NOT exist....");
			System.out.print("\nWould you like to create a new project using: (Y / N)? \n" + projectFolderPath + "\033[1A\033[" + characterSpaces + "C");
			String newProjectUserInpuAnswer = userInput.nextLine();
			if(newProjectUserInpuAnswer.equalsIgnoreCase("y") || newProjectUserInpuAnswer.equalsIgnoreCase("yes")) // WORK ON OTHER INVALID INPUT....
			{	
				File newProject = new File(projectFolderPath);
				newProject.mkdir();

				// Always create a 'Master' branch for every new project....
				File master = new File(projectFolderPath + OSfileSeparator + "Master");
				master.mkdir();

				if(branchName.isEmpty())
				{
					System.out.print("\n\nSave to \'Master\' branch? (Y / N): ");
					newProjectUserInpuAnswer = userInput.nextLine();

					if(newProjectUserInpuAnswer.equalsIgnoreCase("y") || newProjectUserInpuAnswer.equalsIgnoreCase("yes"))
					{
						System.out.print("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to \'Master\' branch for \"" + projectName + "\"....");
						checkAndSaveToBranch(fileOrDirSrc, master.toString(), isFileOrDir);
					}
					else if(newProjectUserInpuAnswer.equalsIgnoreCase("n") || newProjectUserInpuAnswer.equalsIgnoreCase("no"))
					{
						System.out.print("\nNew project \"" + projectName + "\" and \'Master\' branch created...."
								+ "\nWhich new branch would you like to save to: \n" + projectFolderPath + OSfileSeparator);

						// Saving to a new project and a new branch for the new project....
						String newBranchPath = projectFolderPath + OSfileSeparator + userInput.nextLine();
						checkAndSaveToBranch(fileOrDirSrc, newBranchPath, isFileOrDir);
					}
					else // catch for any other input besides yes or no: y/n.
					{

					}
				}
				else if(!branchName.isEmpty()) // New project created and user has entered a branch name for a project that did NOT exist....
				{
					// Calculate [54C by getting the length of newBranchPath....
					System.out.printf("\nWould you like to create a new branch using: (Y / N): \n" + branchName + "\033[F\033[54C");
					String newBranchUserInputAnswer = userInput.nextLine();
					
					// WORK ON OTHER INVALID INPUT....
					if(newBranchUserInputAnswer.equalsIgnoreCase("y") || newBranchUserInputAnswer.equalsIgnoreCase("yes"))
					{
						File newBranch = new File(branchName);
						newBranch.mkdir();
						checkAndSaveToBranch(fileOrDirSrc, branchName, isFileOrDir);
					}
					if(newBranchUserInputAnswer.equalsIgnoreCase("n") || newBranchUserInputAnswer.equalsIgnoreCase("no"))
					{
						System.out.print("\n\nSave to \'Master\' branch? (Y / N): ");
						newProjectUserInpuAnswer = userInput.nextLine();
						
						if(newProjectUserInpuAnswer.equalsIgnoreCase("y") || newProjectUserInpuAnswer.equalsIgnoreCase("yes"))
						{
							System.out.print("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to \'Master\' branch for \"" + projectName + "\"....");
							checkAndSaveToBranch(fileOrDirSrc, master.toString(), isFileOrDir);
						}
						else if(newProjectUserInpuAnswer.equalsIgnoreCase("n") || newProjectUserInpuAnswer.equalsIgnoreCase("no"))
						{
							System.out.println("\nNew project \"" + projectName + "\" and \'Master\' branch created...."
									+ "\nBranch name \"" + branchName + "\" was passed in but user did NOT want to use name to create a branch with it...."
									+ "\nUser did NOT want to save to the \'Master\' branch either...."
									+ "\nTo create a new PROJECT with a new BRANCH, run the program again with certain names...."
									+ "\nKGVersionControl terminated.");
							System.exit(0);
						}
					}
					System.out.println();
				}
			}
			else if(newProjectUserInpuAnswer.equalsIgnoreCase("no") || newProjectUserInpuAnswer.equalsIgnoreCase("n")) // WORK ON OTHER INVALID INPUT....
			{
				System.out.println();
				System.out.println("\nTo create a new PROJECT, run the program again with a certain"
						+ "\nPROJECT name and a certain new BRANCH name, if desired -- and if the PROJECT and BRANCH do NOT already exist!\n");
				System.exit(0);
			}
		}
	}
	
	// Saves files/folders to any branch.... 
	// Method should check for OS and perhaps retrieve local properties....
	private static void checkAndSaveToBranch(File fileOrDirSrc, String branchPath, String isFileOrDir) {
		
//		 Date dateAndTimeStamp = new Date();
//		 DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//		 df.setTimeZone(TimeZone.getDefault());
//		 String dayCreated = df.format(dateAndTimeStamp).toString().substring(0, 10);
		
		if(branchPath.endsWith("Master"))
		{
			File masterBranch = new File(branchPath);
			masterBranch.setWritable(true);
			if(masterBranch.listFiles().length == 0)
			{
				String initBranch = branchPath + OSfileSeparator + "Initial";
				try {
					copyDirectory(fileOrDirSrc, new File(initBranch));
//					Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + " " + initBranch);
//					p.waitFor();
//					p.destroy();
					// if process did not execute, catch error here....
				} catch (Exception e) {
					System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
				}
			}
			else if(masterBranch.listFiles().length > 0)
			{
				int commitNumber = 1;
				for(File files: masterBranch.listFiles()) {
					if(files.getName().startsWith("Commit"))
					{
						if(Integer.parseInt(files.getName().charAt(7) + "") > commitNumber)
						{
							commitNumber = Integer.parseInt(files.getName().charAt(7) + "");
						}
					}
				}
				String commitBranches = branchPath + OSfileSeparator + "Commit(" + (commitNumber+1) + ")";
				try { 
					// Go back & use the "~" home user shortcut 
					// security reason of using full absolute path
					copyDirectory(fileOrDirSrc, new File(commitBranches));
//					Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + " " + commitBranches);
//					p.waitFor();
//					p.destroy();
					// if process did not execute, catch error here....
				} catch (Exception e) {
					System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
				}
			}
			masterBranch.setWritable(false);			
		}
		else // if user saves to another branch that is NOT the 'Master' branch....
		{
			try {
				// copyDirectory(fileOrDirSrc, new File(branchPath));
				System.out.println("\nSource: " + fileOrDirSrc.getAbsolutePath() + "/");
				System.out.println("Dest: " + branchPath);
				copyDirectory(fileOrDirSrc, new File(branchPath));
				// This process & command replaces files, does NOT make another copies if a file already exists....
//				Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + "/ " + branchPath);
//				p.waitFor();
//				p.destroy();
				// if process did not execute, catch error here....
			} catch (Exception e) {
				System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
			}
		}
		System.out.println("\nFiles were successfully saved and copied to their appropriate version.");
	}
	
	
	private static void copyDirectory(File sourceDir, File targetDir) throws IOException, InterruptedException {
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir);
		} else {
			// Check if a file has been modified....
			// Ask user to save all files or only modified files?....
			if(sourceDir.exists())
			{
				DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
				df.setTimeZone(TimeZone.getDefault());
				
				Date lastModified = new Date(sourceDir.lastModified());
				String lastModifiedDate = df.format(lastModified).toString().substring(0, 10);
				
				Date todaysDate = new Date(System.currentTimeMillis());
				String today = df.format(todaysDate).toString().substring(0, 10);
				
				System.out.println("Last Modified Date: " + lastModifiedDate);
				System.out.println("Today's Date: " + today);
				if(lastModifiedDate.equals(today))
				{
//					System.out.println("File: " + targetDir);
//					Files.copy(sourceDir.toPath(), targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
					Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
					p.waitFor();
					p.destroy();
				}
			}
			else
			{
//				System.out.println("File: " + targetDir);
//				Files.copy(sourceDir.toPath(), targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
				p.waitFor();
				p.destroy();
			}
		}
	}
	
	private static void copyDirectoryRecursively(File source, File target) throws IOException, InterruptedException {
		if (!target.exists()) {
			target.mkdir();
			for (String child : source.list()) {
				
				copyDirectory(new File(source, child), new File(target, child));
			}
		}
		else if(target.exists())
		{
				for (String child : source.list()) {
					
					copyDirectory(new File(source, child), new File(target, child));
				}
		}
	}
}
