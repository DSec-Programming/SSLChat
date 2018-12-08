package client.connection;

import java.io.IOException;

/**
 * Spezifikation:
 * 
 * Dieses Runnable sendet ein Object O
 * �ber die PASSIVE Klasse @ClientConnection.
 * 
 * Die Klasse @ClientConnection stellt hierf�r die methode
 * --- void send(Object o){} zur Verf�gung
 * 
 * Ausgef�hrt wird das Runnable �ber den ThreadPool der Klasse @ClientController
 */

public class RunnableSendObject implements Runnable
{

	private ClientConnection connection;
	private Object toSend;

	public RunnableSendObject(ClientConnection connection, Object o)
	{
		// TODO Auto-generated constructor stub
		this.connection = connection;
		this.toSend = o;
	}

	@Override
	public void run()
	{
		try
		{
			this.connection.send(this.toSend);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
