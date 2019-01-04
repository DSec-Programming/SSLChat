package client.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.ArrayList;
import client.model.ClientDataModel;
import streamedObjects.Sendable;
import streamedObjects.UpdateFromServer;

public class ReceiverThread extends Thread
{
	private ObjectInputStream ois;
	private ClientDataModel clientDataModel;
	private ReceiveList receiveList;
	private ArrayList<Long> idList;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public ReceiverThread(InputStream s, ClientDataModel model, ReceiveList receiveList, ArrayList<Long> idList)
			throws IOException
	{
		this.ois = new ObjectInputStream(s);
		this.clientDataModel = model;
		this.receiveList = receiveList;
		this.idList = idList;
	}

	@Override
	public void run()
	{
		try
		{
			while (!this.isInterrupted())
			{
				Sendable s = (Sendable) this.ois.readObject();

				if (this.idList.contains(s.getID()))
				{
					//falls ja warttet irgendwer darauf das er es abhohlt
					
					this.receiveList.add(s);
					continue;
					
				} else
				{
					//keiner wartet darauf dieses Object abzuholen daher Broadcast
					//==> aktuallisiere das Model
					if (s instanceof UpdateFromServer)
					{
						UpdateFromServer update = (UpdateFromServer) s;
						if (update.getUpdateType().equals(CHAT_UPDATE))
						{
							// chatUpdate
							this.clientDataModel.updateMessageToChat(update.getUpDate());
							
						} else if (update.getUpdateType().equals(USER_UPDATE))
						{
							// userupdate
							this.clientDataModel.updateUserInOnlineList(update.getUpDate());
						} 
						else
						{
							System.out.println("RUNTIMEEXECEPTION");
							throw new RuntimeException("NICHT ERWARTETES OBJECT VOM SERVER");
						}
					}
				}
			}
			this.ois.close();
		} 
		catch(SocketException se)
		{			
			if(se.getMessage().equals("Connection reset"))
			{
				clientDataModel.addNotification("SERVER OFFLINE !");
			}		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
