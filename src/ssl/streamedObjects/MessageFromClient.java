package ssl.streamedObjects;

import java.io.Serializable;

public class MessageFromClient implements Serializable
{
	public static final long serialVersionUID = 0L;

	private String msg;

	public MessageFromClient(String msg)
	{
		this.msg = msg;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
