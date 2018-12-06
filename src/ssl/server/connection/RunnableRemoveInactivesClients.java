package ssl.server.connection;

import java.util.ArrayList;

import ssl.server.model.ServerDataModel;

public class RunnableRemoveInactivesClients implements Runnable
{
	private ServerDataModel model;

	public RunnableRemoveInactivesClients(ServerDataModel model)
	{
		this.model = model;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(100);
				
				ArrayList<SingleClientConnection> list = this.model.getAllOpenSingleClientConnections();
				for(SingleClientConnection scc : list)
				{
					if(scc.getSocket().isClosed())
					{
						this.model.removeSingleClientConnection(scc);
					}
				}
				
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
