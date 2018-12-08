package streamedObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateFromServer implements Serializable
{
	public static final long serialVersionUID = 0L;

	private String updateType;
	private ArrayList<String> UpDate;

	public UpdateFromServer(String updateType, ArrayList<String> update)
	{
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
}
