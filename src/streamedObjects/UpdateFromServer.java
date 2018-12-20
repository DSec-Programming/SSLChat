package streamedObjects;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class UpdateFromServer extends Sendable implements Serializable
{
	public static final long serialVersionUID = 0L;

	private String updateType;
	private ArrayList<String> UpDate;

	private long id;
	private long timeStamp;

	public UpdateFromServer(String updateType, ArrayList<String> update)
	{
		SecureRandom rand = new SecureRandom();
		this.id = rand.nextLong();
		this.timeStamp = System.currentTimeMillis();

		this.updateType = updateType;
		this.UpDate = update;
	}

	public String getUpdateType()
	{
		return updateType;
	}

	public void setUpdateType(String updateType)
	{
		this.updateType = updateType;
	}

	public ArrayList<String> getUpDate()
	{
		return UpDate;
	}

	public void setUpDate(ArrayList<String> upDate)
	{
		UpDate = upDate;
	}

	@Override
	public long getID()
	{
		return this.id;
	}

	@Override
	public long getTimeStamp()
	{
		return this.timeStamp;
	}

	@Override
	public boolean wantAnswer()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
