package ssl.client.connection;

import java.util.concurrent.Callable;

public class CallableReceiveServerInfos implements Callable<Boolean>
{
	private ClientConnection connection;

	public CallableReceiveServerInfos(ClientConnection connection)
	{
		this.connection = connection;
	}

	@Override
	public Boolean call() throws Exception
	{
		while (true)
		{
			try
			{
				Thread.sleep(50);
				connection.waitReceiveAndUpdateModel();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
