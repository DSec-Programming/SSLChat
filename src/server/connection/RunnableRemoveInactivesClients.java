package server.connection;

import java.io.IOException;
import java.util.ArrayList;

import server.model.ServerDataModel;

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
			ArrayList<SingleClientConnection> list = this.model.getAllOpenSingleClientConnections();
			SingleClientConnection current = null;

			// SEHR SEHR unschön ...
			String s;
			try
			{
				Thread.sleep(700);

				for (SingleClientConnection scc : list)
				{
					current = scc;
					if (scc.isClientStillAlive())
					{
						System.out.println("REMOVE");
						this.model.removeSingleClientConnection(scc);
					}
				}

			} catch (InterruptedException | IOException | ClassNotFoundException e)
			{
				this.model.removeSingleClientConnection(current);
			}
		}
	}
}
