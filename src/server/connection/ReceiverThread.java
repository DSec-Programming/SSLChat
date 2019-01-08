package server.connection;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import server.model.ConnectionModel;
import server.model.ServerDataModel;
import streamedObjects.ClientHello;
import streamedObjects.ClientSaysBye;
import streamedObjects.MessageFromClient;
import streamedObjects.Sendable;

public class ReceiverThread extends Thread
{
	private SingleClientConnection2 motherConnection;
	private ObjectInputStream ois;
	private ServerDataModel serverDataModel;
	private ConnectionModel cmodel;
	private ReceiveList receiveList;
	private ArrayList<Long> idList;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public ReceiverThread(SingleClientConnection2 motherCon, InputStream s, ServerDataModel model,
			ConnectionModel cmodel, ReceiveList receiveList, ArrayList<Long> idList)
	{
		this.motherConnection = motherCon;
		this.cmodel = cmodel;
		try
		{
			this.ois = new ObjectInputStream(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		this.serverDataModel = model;
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
				Sendable requestFromClient = (Sendable) this.ois.readObject();

				if (this.idList.contains(requestFromClient.getID()))
				{
					//falls ja warttet irgendwer darauf das er es abhohlt

					this.receiveList.add(requestFromClient);
					continue;

				} else
				{
					//keiner wartet darauf dieses Object abzuholen daher Broadcast
					//==> aktuallisiere das Model
					/**
					 * Wieder sehr statisch geht schöner !!
					 **/

					if (requestFromClient instanceof ClientHello)
					{
						this.motherConnection.setUserName(((ClientHello) requestFromClient).getUsername());
						this.cmodel.addSingleClientConnection(motherConnection);

					} else if (requestFromClient instanceof MessageFromClient)
					{
						MessageFromClient msgFromClient = (MessageFromClient) requestFromClient;
						String preparedString = "<" + this.motherConnection.getUsername() + ">:  "
								+ msgFromClient.getMsg();
						this.serverDataModel.addMsgAtChat(preparedString);
					} else if (requestFromClient instanceof ClientSaysBye)
					{
						cmodel.removeSingleClientConnection(this.motherConnection);
						//HUUUIIIIIIII schwer ^^ 
						String s;
						//was passiert hier ??
						this.motherConnection.stop(requestFromClient);

					} else
					{
						throw new RuntimeException("NICHT ERWARTETES OBJECT VOM CLIENT");
					}
					new RuntimeException("NICHT ERWARTETES OBJECT VOM CLIENT");

				}

			}
			this.ois.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
