package client.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime
{
	private LocalDateTime date;
	
	private DateTimeFormatter df;
	
	public DateTime()
	{
		df = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");
	}
	
	public String getDateAndTime()
	{
		date = LocalDateTime.now();
		String dateAndTime = "<" + date.format(df) + "> ";
		return dateAndTime;
	}
	
	public String getTime()
	{
		df = DateTimeFormatter.ofPattern("kk:mm");
		date = LocalDateTime.now();
		String time = " (" + date.format(df) + ")";
		return time;
	}
}
