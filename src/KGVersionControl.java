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
		
		*  Create a string class for commands, preferences, and common phrases....
 * 
 */

public class KGVersionControl {
	
	private static final Scanner userInput = new Scanner(System.in);
	private static final String OSfileSeparator = File.separator;;
	private static Preferences systemPreferences;
	private static String sysPrefWorkSpaceDirectory;
	private static String sysPrefDirectoryDepthNumber;
	private static ArrayList<String> commandWordsList;

	public static void main(String[] args) throws BackingStoreException {
		
		// Quick and easy way to test out the program with hard coded argument user-inputs....
		args = args.clone();
//		String args0 = (args[0] == null) ? "" : args[0];
//		String args1 = (args[1] == null) ? " " : args[1];
//		String args2 = (args[2] == null) ? " " : args[2];
//		String args3 = (args[3] == null) ? " " : args[3];
//		System.out.println("args[0] = " + args0);
//		System.out.println("args[1] = " + args1);
//		System.out.println("args[2] = " + args2);
//		System.out.println("args[3] = " + args3);
//		System.exit(0);
		// args[0] = "save";
		// args[1] = "file/dir Src";
		// args[2] = "NewProject";
		// args[3] = "TestBranch";
		
		// Creates/Loads the program's preferences file and then clears it to remove workspace directory FOR TESTING....
		systemPreferences = Preferences.userNodeForPackage(KGVersionControl.class);
		// systemPreferences.put("directoryDepth", "1");
		
		sysPrefWorkSpaceDirectory = systemPreferences.get("workspace", "");
		sysPrefDirectoryDepthNumber = systemPreferences.get("directoryDepth", "1");
		
		// systemPreferences.clear();
		
		
		// Create the list of commands....
		commandWordsList = new ArrayList<String>();
		commandWordsList.add("~KGCom");
		commandWordsList.add("copy");
		commandWordsList.add("merge");
		commandWordsList.add("preferences"); // add workspace command to preferences.... add which projects/branches to password protect....
		commandWordsList.add("retrieve");
		commandWordsList.add("save");
		commandWordsList.add("view");
		commandWordsList.add("workspace");
		
		// commandWordsList.add("comment"); // Allows user to make comments to branches/projects....
		// commandWordsList.add("create");  // ....
		// commandWordsList.add("git");     // Execute Github commands and can save to your repository's....
		
		// For MacOS....
		String workspace = "~" + sysPrefWorkSpaceDirectory.substring(sysPrefWorkSpaceDirectory.lastIndexOf("/")+1, sysPrefWorkSpaceDirectory.length());
		
		// Different conditions for args.length == 0, 1, & 2
		// if args[1] is , the \n will be else if
		if(args.length == 0)
		{
			// Use string format and perhaps regex....
			System.out.println("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -\n"
					+ "Run the application with the following key words in the order shown below:\n\n"
					+ "\t\t   [command]: [fileSrc/dirSrc], [project], or [branch]\n\n"
					+ "The KG-Version-Control Program only accepts 4 or less parameters at\n"
					+ "a time and must be in order that they appear as shown above....\n\n"
					+ "[Command] is the operation to choose to execute....\n"
					+ "For a list of different commands, key in and enter: ~KGCom\n\n"
					+ "[FileSrc/DirSrc] is the file or folder to be copied and saved to\n"
					+ "the backup workspace directory that is shown at the very beginning\n"
					+ "beginning of the KG Version Control app....\n\n"
					+ "[Project] can either be the name of an existing project or can create\n"
					+ "a new project with a different name, if it does not exist....\n\n"
					+ "[Branch] can either be the name of an existing branch or can create\n"
					+ "a new branch with a different name, if it does not exsit....\n\n"
					+ "~KG Version Control program terminated.\n\n");
			System.exit(0);
		}
		else if(args.length >= 1 && !commandWordsList.contains(args[0]))
		{
			System.out.println("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -"
					+ "\nKGVC Parameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[srcPath] = " + args[1]
					+ "\n[project] = "
					+ "\n[branch]  = "
					+ "\n\nKGVC Error Message:\n"
					+ "Invalid [command] for the KG Version Control program....\n"
					+ "For a list of [command]s key in and enter: ~KGCom\n"
					+ "~KG Version Control program terminated.\n\n");
		}
		else if(args.length == 1 && commandWordsList.contains(args[0]))
		{
			// Ignore case for these commands?....
			if(args[0].equalsIgnoreCase("~KGCom"))
			{
				printOutDescriptionOfCommands();
				System.exit(0);
			}
			else if(args[0].equalsIgnoreCase("view"))
			{
				displayAllProjectsAndBranches();
				System.exit(0);
			}
			else if(args[0].equalsIgnoreCase("workspace")) // delete
			{
				// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
				//  DELETE THIS CONDITION, WORKSPACE WILL BE ADD UNDER PREFERENCES.....!!!!
				// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				
				System.out.print("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -"
						+ "\nRetrieving workspace directory....\n");
				if(sysPrefWorkSpaceDirectory.isEmpty())
				{	
					sysPrefWorkSpaceDirectory = enforceValidWorkspaceDirectory(systemPreferences);
					System.exit(0);
				}
				changeWorkSpace(systemPreferences, sysPrefWorkSpaceDirectory);
				System.exit(0);
			}
			
			System.out.println("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -"
					+ "\nKGVC Parameters:"
					+ "\n[command] = " + args[0] 
					+ "\n[srcPath] = "
					+ "\n[project] = "
					+ "\n[branch]  = "
					+ "\n\nKGVC Error Message:\n"
					+ "Enter the following parameters for the [command]: " + args[0] + "....\n"
					+ "For a list of [command] keys and parameters, enter in: ~KGCom\n"
					+ "~ KG Version Control program terminated. ~\n\n");
			System.exit(0);
		}
		else if(args.length >= 5)
		{
			System.out.println("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -"
					+ "\nKGVC Parameters:"
					+ "\n(1) [command] = " + args[0] 
					+ "\n(2) [srcPath] = " + args[1]
					+ "\n(3) [project] = " + args[2]
					+ "\n(4) [branch]  = " + args[3]
					+ "\n\nError:"
					+ "\n(5) [blocked] = **********"
					+ "\n(6) [blocked] = **********"
					+ "\n(7) ...."
					+ "\n\nKGVC Error Message:\n"
					+ "More than 4 parameters were entered to the KG Version Control\n"
					+ "program. To access the KG Version Control program manual guide,\n"
					+ "run the program with no parameters....\n"
					+ "~KG Version Control program terminated.\n\n");
			System.exit(0);
		}
		
		System.out.println("\n\n\t\t\t- KG VERSION CONTROL PROGRAM -"
				+ "\nRetrieving workspace directory....");
		
		// Initially thought about using regex here....
		if(sysPrefWorkSpaceDirectory.isEmpty())
		{			
			// May need classes to capture OS specifics later in development....
			// Password protected?....
			sysPrefWorkSpaceDirectory = enforceValidWorkspaceDirectory(systemPreferences);
		}
		
		System.out.println("Workspace Directory: \n\n\t" + sysPrefWorkSpaceDirectory + "\n");
		
		// if args[0] is a [command] and more than 2 parameters were entered 
		if(commandWordsList.contains(args[0]) && args.length >= 2)
		{
			// List of Commands....
			// Alphabetical Order....
			// Replace commands with a switch statement?....
			
			// Copy command....
			// Copy can save to any directory within the user's computer....
			// [project] becomes [desPath] signifying for the destination path to copy the [fileSrc/dirSrc] too....
			// copy does NOT need a [branch] parameter.....
			// Make copy a preferences.... 
			// if (user input equals copy && preferencesCopy == True/False
			if(args[0].equalsIgnoreCase("copy"))
			{
				// also check for if [fileSrc/dirSrc] is valid so to print out as an error??...
				if(args.length == 2)
				{
					System.out.println("\nKGVC Parameters:"
							+ "\n[command] = " + args[0] 
							+ "\n[srcPath] = " + args[1]
							+ "\n[desPath] = "
							+ "\n\nKGVC Error Message:");
					
					Path fileOrDirPath = Paths.get(args[1]);
					if(Files.notExists(fileOrDirPath))
					{
						System.out.println("The [srcPath] passed in is INVALID....\n");
					}
					System.out.println("No [desPath] was passed to the KGVC program to copy [srcPath] over....\n"
							+ "~ KG VERSION CONTROL PROGRAM TERMINATED. ~\n\n");
					System.exit(0);
				}
				else if(args.length == 3) // Has both fileSrc/dirSrc & destSrc 
				{
					// Check if [fileSrc/dirSrc] and [desSrc] are valid paths....
					// Branch is NO longer needed since user can just enter the entire branch path for [project]/[desPath]....
					// Since no [branch] is needed anymore, only check if [desPath] is valid....
					// if [desPath] is valid, copy file/dir....
					// else if [desPath] is invalid, check if previous directory path is valid, which would be the project since 
					// the current directory entered is not valid, must mean a branch o.... THIS IS IDEA FOR THE SAVE COMMAND....
					File fileOrDirSrc = new File(args[1]);
					File destSrc = new File(args[2]);
					String isFileOrDir = fileOrDirSrc.isFile() ? "file" : "directory";
					Path srcPath = Paths.get(fileOrDirSrc.getAbsolutePath());
					Path destPath = Paths.get(destSrc.getAbsolutePath());
					if(Files.exists(srcPath) && Files.notExists(destPath))
					{
						// Program allows user to create a branch if system preferences permit it....
						// Also, depending on the number of depth the KGVC is permitted to access directories....
						// [desPath] projectName/branchName.... in this case, branch name does NOT exist....
						// Encapsulate this into a method()....
//						if(KGVCpreferences.copy == True)
//						{
							// loop back through directories to find which path exists....
							String destProjectPath = (sysPrefWorkSpaceDirectory + OSfileSeparator + args[2]);
							String[] pathTokens = args[2].split("/");
//							String[] pathTokens = args[2].substring(args[2].indexOf("/")+1, args[2].length()).split("/");
							for(int i = 0; i < pathTokens.length; i++)
							{
								System.out.println("Token: " + pathTokens[i]);
							}
							String newTrimmedPaths = destProjectPath;
							for(int depth = 0; depth < (pathTokens.length-1); depth++)
							{
								newTrimmedPaths = newTrimmedPaths.substring(0, newTrimmedPaths.lastIndexOf("/"));
								String branch_es = destProjectPath.substring(newTrimmedPaths.lastIndexOf("/", newTrimmedPaths.length()));
								Path validDir = Paths.get(newTrimmedPaths);
								if(Files.exists(validDir)) // testProject/testBranch/a/b is valid....
								{
									
									// System preference depth....
//									if(preferenceDepth == 1)
//									{
										System.out.print("No such [branch] exists within the [project]....\n" 
												+ "Create a new [branch] using: (Y / N): \n" + (workspace + branch_es) + "\033[1A\033[9C");
										String createBranch = userInput.nextLine();
										if(createBranch.equalsIgnoreCase("y") || createBranch.equalsIgnoreCase("yes"))
										{
											// Create new branch folder....
//											File newBranch_es = new File(destProjectPath);
//											newBranch_es.mkdir();

											// Copy this over to the bottom if depth is larger than the default 1....
											// testBranch/2nd/3rd
											// Make sure to create new line "\n" here.... 
											System.out.println();
											String beginningOfProjectPath = pathTokens[0];
											for(int eachBranch = 1; eachBranch <= pathTokens.length; eachBranch++)
											{
												if(eachBranch >= pathTokens.length)
												{
													break;
												}
												beginningOfProjectPath = beginningOfProjectPath + OSfileSeparator + pathTokens[eachBranch];
												File depthBranches = new File(sysPrefWorkSpaceDirectory + OSfileSeparator + beginningOfProjectPath);
//												System.out.println("Depth: " + depthBranches.getAbsolutePath());
												depthBranches.mkdir();
//												newBranchPaths = args[2].substring(args[2].indexOf("/"), args[2]);
//												File eachNewBranchDepthFolder = new File(workSpaceDirectory + OSfileSeparator + newBranchPaths.substring(0));
											}
											System.exit(0);
										}
										else if(createBranch.equalsIgnoreCase("n") || createBranch.equalsIgnoreCase("no"))
										{
											System.out.println("\n\nKGVC Error Message:\n"
													+ "To create a new [branch] (or branches), run the program again with a certain\n"
													+ "[branch] name -- if the [branch] does NOT already exist....\n"
													+ "~ KG Version Control program terminated. ~\n\n");
											System.exit(0);
										}
										else
										{
											
										}
										// Get cursor back to default....
										System.out.println();
										break;
//									}
//									else if(preferenceDepth >= 2)
//									{
										
//									}
								}
							}
//						}
//						else if(KGVCpreferences.copy == False)
//						{
//							
//						}
					}
//					else if(Files.notExists(srcPath) || Files.notExists(destPath))
//					{
//						System.out.println("\nKGVC Parameters:"
//								+ "\n[command] = " + args[0] 
//								+ "\n[srcPath] = " + args[1]
//								+ "\n[desPath] = " + args[2]
//								+ "\n\nKGVC Error Message:\n"
//								+ "The [fileSrc/dirSrc] path or the [desPath] path is INVALID....\n"
//								+ "~ KG Version Control program terminated. ~\n\n");
//						System.exit(0);
//					}
					else if(Files.exists(srcPath) && Files.exists(destPath))
					{
						try {
							Process p = Runtime.getRuntime().exec("cp -R " + new File(args[1]).getAbsolutePath() + " " + new File(args[2]).getAbsolutePath());
							p.waitFor();
							p.destroy();
						} catch (Exception e) {
							System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
						}
					}
					// else? 
				}
			}
			
			// Save Command....
			// Save is the Version Control feature....
			// CHECK TO SEE IF USER WANTS TO SAVE ONLY A CERTIAN FILE(S) OR AN ENTIRE DIRECTORY....
			// CHECK TO SEE WHCIH FILES WHERE MODIFIED AND ONLY SAVE THOSE?
			if(args[0].equalsIgnoreCase("save"))
			{
				// Check if a project name was not passed....
				if(args.length == 2)
				{
					System.out.println("\nKGVC Parameters:"
							+ "\n[command] = " + args[0] 
							+ "\n[srcPath] = " + args[1]
							+ "\n[project] = "
							+ "\n[branch]  = "
							+ "\n\nKGVC Error Message:\n"
							+ "Missing a name for the [project] parameter....\n"
							+ "To \'save\' a file or a directory, please provide a [project] name....\n"
							+ "~KG Version Control program terminated.\n\n");
					System.exit(0);
				}
				else if(args.length > 4)
				{
					System.out.println("\nKGVC Parameters:"
							+ "\n(1) [command] = " + args[0] 
							+ "\n(2) [srcPath] = " + args[1]
							+ "\n(3) [project] = " + args[2]
							+ "\n(4) [branch]  = " + args[3]
							+ "\n\nError:"
							+ "\n(5) [blocked] = **********"
							+ "\n(6) [blocked] = **********"
							+ "\n(7) ...."
							+ "\n\nKGVC Error Message:\n"
							+ "More than 4 parameters were entered to the KG Version\n"
							+ "Control program. To access the KG Version Control program manual guide, run\n"
							+ "the program with no parameters....\n"
							+ "~KG Version Control program terminated.\n\n");
					System.exit(0);
				}
				
				File fileOrDirSrc = new File(args[1]);
				String isFileOrDir = fileOrDirSrc.isFile() ? "file" : "directory";
				
				if(Files.notExists(fileOrDirSrc.toPath())) 
				{
//					String args3 = (args.length <= 3) ? " " : args[3];
					System.out.println("\nKGVC Parameters:"
							+ "\n[command] = " + args[0] 
							+ "\n[srcPath] = " + args[1]
							+ "\n[project] = " + args[2]
							+ "\n[branch]  = " + args[3]
							+ "\n\nKGVC Error Message:\n"
							+ "Source of [fileSrc/dirSrc] does NOT exist....\n"
							+ "Please make sure [fileSrc/dirSrc] already exists....\n"
							+ "~KG Version Control program terminated.\n\n");
					System.exit(0);
				}
				
				if(args.length == 3)
				{
					checkIfProjectExist(sysPrefWorkSpaceDirectory, fileOrDirSrc, args[2], "", isFileOrDir);
				}
				else if(args.length == 4)
				{
					checkIfProjectExist(sysPrefWorkSpaceDirectory, fileOrDirSrc, args[2], args[3], isFileOrDir);
				}
				 System.exit(0);
			}
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
		// To make the cursor in the terminal/command prompt move up 37 lines....
		System.out.println("\n\n\n\t\t\t\t- KG VERSION CONTROL APP -"
				+ "\n*Parameters placed inside '[]' brackets are mandatory parameters, while parameters"
				+ "\nthat are placed inside the '<>' brackets are optional parameters."
				+ "\nCommand List: "
				+ "\n"
				+ "\n~KGCom : Display the list of commands and their appropriate parameters."
				+ "\n"
				+ "\ncopy [project/file] [project] <branch> : To copy any file or a directory to a\n"
				+ "project within the current workspace for the KG Version Control program. Or can"
				+ "\ncopy a project or copy a branch in order to conduct a quick implementation to\n"
				+ "test a possible new feature or a new project or branch for a complete different\n"
				+ "approach of the project or of the branch that was commanded to be copied/cloned."
				+ "\n"
				+ "\nmerge [srcBranch] [destBranch] : overwrties all files and sub directories from the"
				+ "\n\tdest branch and copies over any files and/or any sub directories to the dest"
				+ "\n\tbranch from the src branch."
				+ "\n"
				+ "\nretrieve [project] <branch> <file>: retrieves a file or directory that may no longer"
				+ "\n\texist in the workspace. Can retrieve a file or directories from a month ago,"
				+ "\n\t3 months ago, 6 months ago, or a from year ago. Retrieve a file or a directory,"
				+ "\n\tfrom a the project or a branch, and any associated sub directories."
				+ "\n"
				+ "\nsave [fileSrc/dirSrc] [project] <branch> : copy fileSrc/dirSrc to an existing project"
				+ "\n\twithin the back up workspace. If no project exists, program can create a new"
				+ "\n\tnproject with the entered name. Pass in a branch to save a copy to a project"
				+ "\n\tor save to the project's \'Master\' branch."
				+ "\n"
				+ "\nview <project> <branch> : view all projects. If a project's name is passed, view all"
				+ "\n\tthe branches of the project. If a project's name and a branch's name is passed,"
				+ "\n\tview all of the branch's files and sub directories."
				+ "\n"
				+ "\nworkspace : change the current back up workspace directory path.\n\n\n");
	}

	
	private static void displayAllProjectsAndBranches() {}
	
	private static void changeWorkSpace(Preferences systemPreferences, String workSpaceDirectory) {
		
		// Password protected?....
		System.out.println("Workspace Directory: \n\n\t" + workSpaceDirectory);
		System.out.print("\nChange backup workspace directory path? (Y / N): ");
		String changeWorkSpaceDir = userInput.nextLine();
		
		if(changeWorkSpaceDir.equalsIgnoreCase("y") || changeWorkSpaceDir.equalsIgnoreCase("yes"))
		{
			String userHomePath = System.getProperty("user.home") + OSfileSeparator;
			System.out.print("Enter new backup workspace directory: \n" + userHomePath);
			workSpaceDirectory = userInput.nextLine();
			
			Path newWorkSpaceDir = Paths.get(userHomePath + workSpaceDirectory);
			if(!Files.exists(newWorkSpaceDir))
			{
				System.out.println("\nThe workspace directory entered does NOT exist, INVALID path....\n"
						+ "Make sure the valid directory already exists before trying to switch workspaces.\n"
						+ "~KG Version Control program terminated.\n\n\n");
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
				System.out.println("\n\nRun the program again with a certain new backup workspace directory.\n"
						+ "~KG Version Control program terminated.\n\n\n");
				System.exit(0);
			}
		}
		else if(changeWorkSpaceDir.equalsIgnoreCase("n") || changeWorkSpaceDir.equalsIgnoreCase("no"))
		{
			System.out.println("\nNo changes were made to the backup workspace directory.\n"
					+ "~KG Version Control program terminated.\n\n\n");
			System.exit(0);
		}
		else
		{
			
		}
	}
	
	// Prompts user to enter a workspace directory and validates whether path exists or not.... 
	
	private static String enforceValidWorkspaceDirectory(Preferences systemPreferences) {

		// Use regex here?....
		// Password protected?....
		String userHomePath = System.getProperty("user.home") + OSfileSeparator;
		System.out.print("\nWorkspace was NOT dectected....\n"
				+ "Please enter a valid backup workspace directory path to save\n"
				+ "development artifacts and system preferences....\n\n"
				+ "Workspace: " + userHomePath);
		String workSpaceDirectory = userHomePath + userInput.nextLine();
	
		// Validates if workspace path exits....
		Path workSpacePath  = Paths.get(workSpaceDirectory);
		if(Files.exists(workSpacePath))
		{
			System.out.println("\nBackup workspace directory path is valid....\n"
					+ "Setting up new backup workspace directory....");
			systemPreferences.put("workspace", workSpaceDirectory); // Saves the workspace path to the program's preferences file....
		}
		else 
		{
			// if user's input workspace path is INVALID, prompt user to try again until Files.exists() validates path?....
			// Update and work on later....
			while(Files.exists(workSpacePath) == false)
			{					
				System.out.println("\nKGVC Error Message:\n"
						+ "Invalid directory....\n"
						+ "Check for any typos and try again....\n"
						+ "Workspace: " + userHomePath);
				workSpaceDirectory = userHomePath + userInput.nextLine();
				workSpacePath = Paths.get(workSpaceDirectory);
			}
			
			// Saves workspace path to the program's preferences file once Files.exist validates user's input workspace path....
			System.out.println("\nBackup workspace directory path is valid....\n"
					+ "Setting up new backup workspace directory...");
			systemPreferences.put("workspace", workSpaceDirectory);
		}
		System.out.println("New backup Workspace Directory: \n\n\t" + workSpaceDirectory + "\n\n");
		return workSpaceDirectory;
	}
	
	
	// Method checks if a project exists due to user input parameters & system preferences....
	private static void checkIfProjectExist(String workSpaceDirectory, File fileOrDirSrc, String projectName, String branchName, String isFileOrDir) {
		
		// Establish project path....
		String projectFolderPath = workSpaceDirectory + OSfileSeparator + projectName;
		Path path = Paths.get(projectFolderPath);

		String nameOfFileOrDir = fileOrDirSrc.toString().substring(fileOrDirSrc.toString().lastIndexOf("/") + 1, fileOrDirSrc.toString().length());
		System.out.println("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to workspace....\n"
				+ "Retrieving [project]: \"" + projectName + "\"....");

		// Check to see if project exists....
		if(Files.exists(path))
		{
			// If a branch was NOT passed....
			if(branchName.isEmpty())
			{
				System.out.print("\nSave to \'Master\' [branch]? (Y / N): ");
				String userMasterBranchAnswer = userInput.nextLine();

				if(userMasterBranchAnswer.equalsIgnoreCase("y") || userMasterBranchAnswer.equalsIgnoreCase("yes")) // WORK ON OTHER INVALID INPUT....
				{
					// Saving to the 'Master' branch....
					System.out.print("\nSaving files to \'Master\' [branch]....");

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
					System.out.print("\nProject: " + projectFolderPath + "....\n"
							+ "Which [branch] would you like to save too:\n" + projectFolderPath + OSfileSeparator);
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
						System.out.printf("\nBranch does NOT exist....\n"
								+ "Would you like to create a new [branch] using: (Y / N): \n" + newBranchPath + "\033[F\033[54C");
						String userNewBranchAnswer = userInput.nextLine();
						
						// WORK ON OTHER INVALID INPUT....
						if(userNewBranchAnswer.equalsIgnoreCase("y") || userNewBranchAnswer.equalsIgnoreCase("yes"))
						{
							checkAndSaveToBranch(fileOrDirSrc, newBranchPath, isFileOrDir);
						}
						else if(userNewBranchAnswer.equalsIgnoreCase("n") || userNewBranchAnswer.equalsIgnoreCase("no"))
						{
							System.out.println("\nKGVC Error Message:\n"
									+ "To create a new [branch], run the program again with a\n"
									+ "certain [branch] name -- if the [branch] does not exist already....\n"
									+ "~KG Version Control program terminated.\n\n");
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
					System.out.printf("\nBranch does NOT exist....\n"
							// Calculate [54C by getting the length of newBranchPath....
							+ "Would you like to create a new [branch] using: (Y / N): \n" + branchPath + "\033[F\033[54C");
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
						System.out.println("\nKGVC Error Message:\n"
								+ "To create a new [branch], run the program again with a certain\n"
								+ "[branch] name -- if the [branch] does NOT already exist....\n"
								+ "~ KG Version Control program terminated. ~\n\n");
						System.exit(0);
					}
					System.out.println();
				}
			}
		}
		else // else if PROJECT does NOT exist....
		{
			// check system preference for allowing program to create projects....
			// LEFT HERE.... thinking about how to rework these 2 methods I have based of the copyDir() & process methods....
			// Fix character spaces when displaying path below....
			int characterSpaces = "Would you like to create a new [project] using: (Y / N)? \n".length() - projectFolderPath.length();
			// System.out.print("\nProject does NOT exist...."
			// + "\nWould you like to create a new project using: (Y / N): \n" + projectFolderPath + "\033[1A\033[16C");
			System.out.print("\nProject does NOT exist....\n"
					+ "Would you like to create a new [project] using: (Y / N)? \n" + projectFolderPath + "\033[1A\033[" + characterSpaces + "C");
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
					System.out.println("\nSave to \'Master\' [branch]? (Y / N): ");
					newProjectUserInpuAnswer = userInput.nextLine();

					if(newProjectUserInpuAnswer.equalsIgnoreCase("y") || newProjectUserInpuAnswer.equalsIgnoreCase("yes"))
					{
						System.out.print("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to \'Master\' [branch] to [project]: \"" + projectName + "\"....");
						checkAndSaveToBranch(fileOrDirSrc, master.toString(), isFileOrDir);
					}
					else if(newProjectUserInpuAnswer.equalsIgnoreCase("n") || newProjectUserInpuAnswer.equalsIgnoreCase("no"))
					{
						System.out.print("\nNew project [" + projectName + "] and \'Master\' [branch] created....\n"
								+ "Which new [branch] would you like to save to: \n" + projectFolderPath + OSfileSeparator);

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
					System.out.printf("\nBranch did not exist within the " + projectName + " [project]....\n"
							+ "Would you like to create a new [branch] using: (Y / N): \n" + branchName + "\033[F\033[54C");
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
						System.out.print("\nSave to \'Master\' [branch]? (Y / N): ");
						newProjectUserInpuAnswer = userInput.nextLine();
						
						if(newProjectUserInpuAnswer.equalsIgnoreCase("y") || newProjectUserInpuAnswer.equalsIgnoreCase("yes"))
						{
							System.out.print("\nSaving \"" + nameOfFileOrDir + "\" " + isFileOrDir + " to \'Master\' [branch] to [project]: \"" + projectName + "\"....");
							checkAndSaveToBranch(fileOrDirSrc, master.toString(), isFileOrDir);
						}
						else if(newProjectUserInpuAnswer.equalsIgnoreCase("n") || newProjectUserInpuAnswer.equalsIgnoreCase("no"))
						{
							System.out.println("\nKGVC Error Message:\n"
									+ "New project [" + projectName + "] and \'Master\' [branch] created....\n"
									+ "Branch name [" + branchName + "] was NOT valid or used to create a new [branch]....\n"
									+ "User did NOT want to save to the \'Master\' [branch]....\n"
									+ "To create a new [project] with a new [branch], run the KG Version Control program\n"
									+ "again with certain names to create, or retrieve, [project]'s and [branch]'s....\n"
									+ "~ KG Version Control program terminated. ~\n\n");
							System.exit(0);
						}
					}
				}
			}
			else if(newProjectUserInpuAnswer.equalsIgnoreCase("no") || newProjectUserInpuAnswer.equalsIgnoreCase("n")) // WORK ON OTHER INVALID INPUT....
			{
				System.out.println();
				System.out.println("\nKGVC Error Message:\n"
						+ "To create a new [project], run the program again with a\n"
						+ "certain [project] name and a certain new [branch] name,\n"
						+ "if desired -- and if the [project] and [branch] do NOT already\n"
						+ "exist within the workspace for the KG Version Control proram....\n"
						+ "~ KG Version Control program terminated. ~\n\n");
				System.exit(0);
			}
		}
	}
	
	private static void checkAndSaveToBranch(File fileOrDirSrc, String branchPath, String isFileOrDir) {
		
		if(branchPath.endsWith("Master"))
		{
			File masterBranch = new File(branchPath);
			masterBranch.setWritable(true);
			if(masterBranch.listFiles().length == 0)
			{
				String initBranch = branchPath + OSfileSeparator + "Initial";
				try {
//					copyDirectory(fileOrDirSrc, new File(initBranch));
					Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + " " + initBranch);
					p.waitFor();
					p.destroy();
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
//					copyDirectory(fileOrDirSrc, new File(commitBranches));
					Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + " " + commitBranches);
					p.waitFor();
					p.destroy();
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
				Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + "/ " + branchPath);
				p.waitFor();
				p.destroy();
			} catch (Exception e) {
				System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
			}
			
//			Path validBranchPath = Paths.get(branchPath);
//			if(Files.notExists(validBranchPath))
//			{
//				// previous method should check for preferences....
//				// if create branches or create projects preference is turned on....
//				int x = 0; // Delete
//				if(x > 0) // if preference is on....
//				{
//					// could ask user here about either creating a new branch or not.....
//					
//					// if the preference to save only modified files or copy of a file or a copy of an entire directory path....
//					// save to this 
//					if(x > 0) // if preference is on, to save only modified files....
//					{
//						
//					}
//					else // ....
//					{
//						try {
//							
//							// copyDirectory(fileOrDirSrc, new File(branchPath));
////							copyDirectory(fileOrDirSrc, new File(branchPath));
//							// This process & command replaces files, does NOT make another copy if a file already exists....
//							// Check preference to see if save only modified files or to save a new copy of a file or a directory is chosen....
//							// File copyOfFileOrDir = new File(args[]);
//							Process p = Runtime.getRuntime().exec("cp -R " + fileOrDirSrc.getAbsolutePath() + "/ " + branchPath);
//							p.waitFor();
//							p.destroy();
//							// if process did not execute, catch error here....
//						} catch (Exception e) {
//							System.out.println("Could not save the " + isFileOrDir + ": " + e.getMessage());
//						}
//					}
//				}
//				else // if preference is off, 
//				{
//					System.out.println("KGVC Error Message:\n"
//							+ "KGVC preferences are currently restricting the creation of [branches]....\n"
//							+ "~KGVC Version Control program terminated.\n");
//					System.exit(0);
//				}
//				
//			}
		}
		System.out.println("\nFiles were successfully saved and copied to their appropriate version.");
	}
	
	private static void copyDirectory(File sourceDir, File targetDir) throws IOException, InterruptedException {
		System.out.println("TargetSrc: " + targetDir);
//		int allFilesInProject = targetDir.listFiles().length;
//		System.out.println("Num of files: " + allFilesInProject);
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir);
		} else {
			// Check if a file has been modified....
			// Ask user to save all files or only modified files?....
			// So far will overwrite and not add any files that were NOT created or modified today....
			// ^^^ Fix 
			
//			if(allFilesInProject == 0)
//			{
//				Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
//				p.waitFor();
//				p.destroy();
//			}
//			else if(allFilesInProject > 0)
//			{
//				DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//				df.setTimeZone(TimeZone.getDefault());
//				
//				Date lastModified = new Date(sourceDir.lastModified());
//				String lastModifiedDate = df.format(lastModified).toString().substring(0, 10);
//				
//				Date todaysDate = new Date(System.currentTimeMillis());
//				String today = df.format(todaysDate).toString().substring(0, 10);
//				
//				System.out.println("Last Modified Date: " + lastModifiedDate);
//				System.out.println("Today's Date: " + today);
//				if(lastModified.getTime() < todaysDate.getTime())
//				{
////					System.out.println("File: " + targetDir);
////					Files.copy(sourceDir.toPath(), targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
//					Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
//					p.waitFor();
//					p.destroy();
//				}
//			}
			Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
			p.waitFor();
			p.destroy();
		}
//		Files.copy(sourceDir.toPath(), targetDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
//		Process p = Runtime.getRuntime().exec("cp -R " + sourceDir + " " + targetDir);
//		p.waitFor();
//		p.destroy();
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
