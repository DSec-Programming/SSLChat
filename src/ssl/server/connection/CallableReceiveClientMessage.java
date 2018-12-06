package ssl.server.connection;

import java.util.concurrent.Callable;

public class CallableReceiveClientMessage implements Callable<Boolean>
{
	private SingleClientConnection connection;
	
	public CallableReceiveClientMessage(SingleClientConnection connection)
	{
		this.connection = connection;
	}

	@Override
	public Boolean call() throws Exception
	{
		//System.out.println("Call");
		this.connection.receiveClientRequests();
		return true;
	}
}
