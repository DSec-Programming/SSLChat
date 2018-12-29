package streamedObjects;

import java.io.Serializable;
import java.security.SecureRandom;

import client.model.DateTime;

public class MessageFromClient extends Sendable implements Serializable
{
	public static final long serialVersionUID = 0L;

	private String msg;
	
	
	private long id;
	private long timeStamp;

	public MessageFromClient(String msg)
	{
		SecureRandom rand = new SecureRandom();
		this.id = rand.nextLong();
		this.timeStamp = System.currentTimeMillis();
		DateTime time = new DateTime();
		this.msg = msg + time.getTime();
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
