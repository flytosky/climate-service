package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;

public class TimeConvert {

	public static final String timeStamptoDate (String timeStamp) {
		if(timeStamp == ""){
			return "";
		}
		Date date = new Date(Long.parseLong(timeStamp)*1000);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String time = dateFormat.format(date);
		return time;
	}
	
	public static final long datetoTimeStamp (String time) throws ParseException{
		if(time == null || time.equals("")){
			return 0;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		String temptime = time + ":00.000";
		Date parsedDate = dateFormat.parse(temptime);
		long timeStamp = parsedDate.getTime();
		return timeStamp;
		
	}
}
