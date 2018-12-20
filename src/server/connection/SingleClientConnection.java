package server.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import server.model.ConnectionModel;
import server.model.ServerDataModel;
import streamedObjects.ClientHello;
import streamedObjects.ClientSaysBye;
import streamedObjects.MessageFromClient;
import streamedObjects.UpdateFromServer;

/**
 * Spezifikation
 * 
 * Die Klasse SingleClientConnection ist eine PASSIVE Klasse
 * 
 * .....
 *
 */

public class SingleClientConnection
{
	private Socket socket;
	private ObjectOutputStream toClient;
	private ObjectInputStream fromClient;

	private ServerDataModel model;
	private ConnectionModel connectionModel;

	private String username;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public SingleClientConnection(Socket s, ServerDataModel model, ConnectionModel conModel)
			throws IOException, ClassNotFoundException
	{
		this.model = model;
		this.connectionModel = conModel;
		this.socket = s;
		this.toClient = new ObjectOutputStream(this.socket.getOutputStream());
		this.fromClient = new ObjectInputStream(this.socket.getInputStream());

		// ErsterRequest ist "anmeldung" des Users
		this.username = ((ClientHello)this.fromClient.readObject()).getUsername();
	}

	/**
	 * sendet ein update an DIESEN Client
	 **/
	public synchronized void sendUpdate(String updateType, ArrayList<String> update) throws IOException
	{
		this.toClient.writeObject(new UpdateFromServer(updateType, update));
		this.toClient.flush();
	}

	/**
	 * Diese Methode empfängt Nachrichten von den Clients und fügt sie im
	 * ServerDataModel hinzu !
	 **/
	public synchronized void receiveClientRequests() throws IOException, InterruptedException, ClassNotFoundException
	{
//		try
//		{
//			// Kurzen TimeOut setzen, damit der Server merkt, dass nichts auf dem InputStream anliegt --> SocketTimeOutException
//			this.socket.setSoTimeout(50);

			Object requestFromClient = this.fromClient.readObject();

			// wenn geschlossen
			// dann müsste exception fliegen !!!
			// wenn exception fliegt SOcket geschlossen !!
			// wenn request vielleciht null

			/**
			 * Wieder sehr statisch geht schöner !!
			 **/
			if (requestFromClient instanceof MessageFromClient)
			{
				MessageFromClient msgFromClient = (MessageFromClient) requestFromClient;
				String preparedString = "<" + this.username + ">:  " + msgFromClient.getMsg();
				this.model.addMsgAtChat(preparedString);

			} else if (requestFromClient instanceof ClientSaysBye)
			{
				String s;
				//connectionModel.removeSingleClientConnection(this);
				socket.close();

			} else
			{
				throw new RuntimeException("NICHT ERWARTETES OBJECT VOM CLIENT");
			}
//		} catch (SocketTimeoutException e)
//		{
//			return;
//		}

	}

	public synchronized String getUserName()
	{
		return this.username;
	}

	public synchronized Socket getSocket()
	{
		return this.socket;
	}

}
