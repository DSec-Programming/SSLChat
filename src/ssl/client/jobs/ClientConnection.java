package ssl.client.jobs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ssl.client.model.ClientDataModel;

public class ClientConnection
{
	private Socket socket;

	private String user;
	public ClientDataModel model;

	private static final String COMMAND_SENDONLINEUSERS = "sendonlineusers";
	private static final String COMMAND_SENDCHAT = "sendchat";

	public ClientConnection(String serveraddr, int port, String user, ClientDataModel model)
			throws IOException, UnknownHostException
	{
		Socket s = new Socket(serveraddr, port);
		this.socket = s;
		this.user = user;
		// hier wird alles gespeichert was ich vom Server bekomme
		this.model = model;
		// User beim Server "anmelden"
		this.send(user);
	}

	public synchronized void send(String msg) throws IOException
	{
		BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		toServer.write(msg);
		toServer.newLine();
		toServer.flush();
	}

	public synchronized ArrayList<String> receiveOnlineUsers(int timeout) throws IOException
	{
		// sende mir bitte online users
		// ObjectoutputStream !!
		BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		toServer.write(COMMAND_SENDONLINEUSERS);
		toServer.newLine();
		toServer.flush();

		// antwort
		ArrayList<String> returnList = null;
		try
		{
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			returnList = (ArrayList<String>) ois.readObject();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return returnList;
	}

	public synchronized ArrayList<String> receiveChat(int timeout) throws IOException
	{
		BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		toServer.write(COMMAND_SENDCHAT);
		toServer.newLine();
		toServer.flush();
		// sende mir bitte Chat
		// ObjectoutputStream !!

		// antwort
		ArrayList<String> returnList = null;
		try
		{
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			returnList = (ArrayList<String>) ois.readObject();

		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return returnList;
	}

	// damit der ReceiveThread rechtzeitig abbricht !
	public synchronized boolean isClosed()
	{
		return this.socket.isClosed();
	}

	public synchronized void close() throws IOException
	{
		this.socket.close();
	}
}
