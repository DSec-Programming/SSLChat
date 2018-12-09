package client.model;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import client.connection.ClientConnection;
import client.connection.RunnableReceiveServerBroadcasts;
import client.connection.RunnableSendObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import streamedObjects.MessageFromClient;

public class ConnectionModel
{
	private User user;
	private ClientConnection connection;

	private ThreadPoolExecutor pool;
	private RunnableReceiveServerBroadcasts runnableReceiveServerBroadcasts;

	private StringProperty ServerIP;
	private StringProperty usedProvider;

	// ConnectionType e { "SSL","TCP" }
	private StringProperty connectionType;
	// ServerStatus e {"authorized","unauthorized"}
	private StringProperty serverStatus;
	// ClientStatus e {"authorized","unauthorized"}
	private StringProperty clientStatus;

	public ConnectionModel()
	{
		this.user = null;
		this.connection = null;
		
		this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		this.runnableReceiveServerBroadcasts = null;
		
		this.ServerIP = new SimpleStringProperty();
		this.usedProvider = new SimpleStringProperty();

		this.connectionType = new SimpleStringProperty();
		this.serverStatus = new SimpleStringProperty();
		this.clientStatus = new SimpleStringProperty();
	}

	// GETTER

	public synchronized User getUser()
	{
		return user;
	}

	public synchronized StringProperty getServerIP()
	{
		return ServerIP;
	}

	public synchronized StringProperty getUsedProvider()
	{
		return usedProvider;
	}

	public synchronized StringProperty getConnectionType()
	{
		return connectionType;
	}

	public synchronized StringProperty getServerStatus()
	{
		return serverStatus;
	}

	public synchronized StringProperty getClientStatus()
	{
		return clientStatus;
	}

	// SETTER

	public synchronized void setUser(User user)
	{
		this.user = user;
	}

	public synchronized void setConnectionTyp(String ct)
	{
		this.connectionType.set(ct);
	}

	public synchronized void setServerStatus(String status)
	{
		this.serverStatus.set(status);
	}

	public synchronized void setClientStatus(String status)
	{
		this.clientStatus.set(status);
	}

	public synchronized void sendMessageOverClientConnection(MessageFromClient msg)
	{
		this.pool.submit(new RunnableSendObject(this.connection, msg));
	}

	public synchronized void setConnection(ClientConnection c) throws IOException
	{
		this.connection = c;
	}

	public synchronized void startWorkingConnection() throws IOException
	{
		this.runnableReceiveServerBroadcasts = new RunnableReceiveServerBroadcasts(connection);
		this.pool.submit(this.runnableReceiveServerBroadcasts);
		this.connection.send(this.getUser().getUsername());
	}

	public synchronized void killConnectoin() throws IOException
	{
		this.runnableReceiveServerBroadcasts.stopRunning();
		this.connection.close();
		this.connection = null;
	}

}
