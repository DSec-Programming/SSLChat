package server.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import server.model.ServerDataModel;
import streamedObjects.MessageFromClient;
import streamedObjects.Ping;
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

	private String username;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public SingleClientConnection(Socket s, ServerDataModel model) throws IOException, ClassNotFoundException
	{
		this.model = model;
		this.socket = s;
		this.toClient = new ObjectOutputStream(this.socket.getOutputStream());
		this.fromClient = new ObjectInputStream(this.socket.getInputStream());

		// ErsterRequest ist "anmeldung" des Users
		this.username = (String) this.fromClient.readObject();
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
		if (this.socket.getInputStream().available() == 0)
		{
			return;
		}
		Object requestFromClient = this.fromClient.readObject();

		/**
		 * Wieder sehr statisch geht schöner !!
		 **/
		if (requestFromClient instanceof MessageFromClient)
		{
			MessageFromClient msgFromClient = (MessageFromClient) requestFromClient;
			String preparedString = "<" + this.username + ">:  " + msgFromClient.getMsg();
			this.model.addMsgAtChat(preparedString);

		} else
		{
			throw new RuntimeException("NICHT ERWARTETES OBJECT VOM CLIENT");
		}

	}

	public synchronized boolean isClientStillAlive() throws IOException, ClassNotFoundException
	{
		this.toClient.writeObject(new Ping());
		Object o = null;
		this.socket.setSoTimeout(100);
		o = this.fromClient.readObject();
		if (o == null)
		{
			return true;
		} else
		{
			return false;
		}

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
