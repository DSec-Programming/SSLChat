package ssl.client.jobs;

import java.io.IOException;
import java.util.ArrayList;

import ssl.client.model.ClientDataModel;

public class ReceiveThread extends Thread
{
	private ClientConnection connection;
	private ClientDataModel model;

	public ReceiveThread(ClientConnection connection, ClientDataModel model)
	{
	    setDaemon(true);
		this.connection = connection;
		this.model = model;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				// MUSS NOCH GETOMEOUTED werden !!
				synchronized (model)
				{
					// natürlich nur versuchen wenn der socket noch eine
					// verbindung zum Server hat
					if (!this.connection.isClosed())
					{
						ArrayList<String> onlineUsers = connection.receiveOnlineUsers(100);
						model.updateUserInOnlineList(onlineUsers);

						ArrayList<String> chat = connection.receiveChat(100);
						model.updateMessageToChat(chat);
					} else
					{
						break;
					}
				}
			} catch (IOException e)
			{
				// e.printStackTrace();
				break;
			}

		}
		System.out.println("RECEIVE THREAD TERMINATET !!");
	}
}
