package client.connection;


import streamedObjects.Sendable;

/**
 * Spezifikation:
 * 
 * Dieses Runnable sendet ein Object O
 * über die PASSIVE Klasse @ClientConnection.
 * 
 * Die Klasse @ClientConnection stellt hierfür die methode
 * --- void send(Object o){} zur Verfügung
 * 
 * Ausgeführt wird das Runnable über den ThreadPool der Klasse @ClientController
 */

public class RunnableSendObject implements Runnable
{

	private ClientConnection2 connection;
	private Sendable toSend;

	public RunnableSendObject(ClientConnection2 connection, Sendable s)
	{
		// TODO Auto-generated constructor stub
		this.connection = connection;
		this.toSend = s;
	}

	@Override
	public void run()
	{
		this.connection.send(this.toSend);

	}
}
