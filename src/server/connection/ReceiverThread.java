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
		System.out.println("1");
		this.cmodel = cmodel;
		System.out.println("2");
		try
		{
			this.ois = new ObjectInputStream(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("3");
		this.serverDataModel = model;
		System.out.println("4");
		this.receiveList = receiveList;
		System.out.println("5");
		this.idList = idList;
		System.out.println("6");
		System.out.println("KONSTRUKLOT ReceiverThread");
	}

	@Override
	public void run()
	{
		try
		{
			while (!this.isInterrupted())
			{
				System.out.println("Receive Thread wartet auf object");
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
						System.out.println(((ClientHello) requestFromClient).getUsername());
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
						this.motherConnection.stop();

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
