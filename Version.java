

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Version implements Serializable {
	
//	private File file;
//	private ArrayList<Project> history;
 
	private final Date dateAndTimeStamp;
	private final DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	private final String dayCreated;
	private final String timeCreatedForTheSavedDay;
	private final String regularTime;
	
	public Version(String filePath) {
		
//		file = new File(filePath);
//		history = new ArrayList<Project>();
		
		dateAndTimeStamp = new Date();
		// Gets the user's local time zone and formats it to MM-dd-yyyy HH:mm:ss.... 
		// Might need more work as development continues....
		df.setTimeZone(TimeZone.getDefault());
		
		// Sun Dec 12 AND (11, df.format(dateAndTimeStamp).toString().length()) equals the time == 02:14:07 
//		dayCreated = dateAndTimeStamp.toString().substring(11, df.format(dateAndTimeStamp).toString().length());
		
		// 12-13-2021
		dayCreated = df.format(dateAndTimeStamp).toString().substring(0, 10);
		
		//  If 2:57, time in military form is 14:57
		timeCreatedForTheSavedDay = dateAndTimeStamp.toString().substring(11, df.format(dateAndTimeStamp).toString().length() - 3);
		regularTime = getRegularTimeForm(timeCreatedForTheSavedDay);

	}
	String getTime() {
		return regularTime;
	}
	
	String getRegularTimeForm(String militaryTime) {
		// If militaryTime has DOUBLE DIGITS....
		if(Integer.parseInt(militaryTime.substring(0, 2)) <= 9)
		{
			militaryTime = "0" + militaryTime;
		}
		return militaryTime.substring(0, 2) + "-" + militaryTime.substring(3, militaryTime.length());
	}
	
//	void getDate() {
//		System.out.println("File created on: " + df.format(dateAndTimeStamp));
//	}
	
	String getDayCreated() {
		return dayCreated;
	}
}
