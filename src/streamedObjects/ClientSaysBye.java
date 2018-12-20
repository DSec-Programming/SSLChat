package streamedObjects;

import java.io.Serializable;
import java.security.SecureRandom;

public class ClientSaysBye extends Sendable implements Serializable
{
	private static final long serialVersionUID = 0L;

	private long id;
	private long timeStamp;

	public ClientSaysBye()
	{
		SecureRandom rand = new SecureRandom();
		this.id = rand.nextLong();
		this.timeStamp = System.currentTimeMillis();
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
