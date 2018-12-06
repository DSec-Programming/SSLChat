package ssl.server.connection;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import ssl.server.model.ServerDataModel;

public class RunnableObserveSingleClientConnections implements Runnable
{
	private ServerDataModel model;
	private ThreadPoolExecutor pool;

	public RunnableObserveSingleClientConnections(ServerDataModel model, ThreadPoolExecutor pool)
	{
		this.model = model;
		this.pool = pool;
	}

	@Override
	public void run()
	{

		while (true)
		{
			// verzögern
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			ArrayList<SingleClientConnection> connections = this.model.getAllOpenSingleClientConnections();
			for (SingleClientConnection scc : connections)
			{
				this.pool.submit(new CallableReceiveClientMessage(scc));
				//System.out.println("new Submit");
			}

		}

	}
}
