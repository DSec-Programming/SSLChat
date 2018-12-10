package server.connection;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import server.model.ConnectionModel;

public class RunnableObserveSingleClientConnections implements Runnable
{
	private ConnectionModel model;
	private ThreadPoolExecutor pool;

	public RunnableObserveSingleClientConnections(ConnectionModel model, ThreadPoolExecutor pool)
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
				Thread.sleep(50);
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
