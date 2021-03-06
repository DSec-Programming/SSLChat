package server.connection;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class CallableSendBroadcastUpdate implements Callable<Boolean>
{

	private SingleClientConnection2 connection;
	private String updateType;
	private ArrayList<String> update;

	public CallableSendBroadcastUpdate(SingleClientConnection2 connection, String updateType, ArrayList<String> update)
	{
		this.connection = connection;
		this.updateType = updateType;
		this.update = update;
	}

	@Override
	public Boolean call() throws Exception
	{
		this.connection.sendUpdate(updateType, update);
		return true;
	}
}
