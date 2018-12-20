package streamedObjects;

import java.io.Serializable;

public class ClientHello extends Sendable implements Serializable
{
	private static final long serialVersionUID = 0L;

	private String username;

	public ClientHello(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return this.username;
	}

	@Override
	public long getID()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeStamp()
	{
		return 0;
	}

	@Override
	public boolean wantAnswer()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
