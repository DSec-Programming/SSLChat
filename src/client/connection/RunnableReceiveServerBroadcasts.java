package client.connection;

import java.io.IOException;
import java.net.SocketException;

/**
 * Spezifikation:
 * 
 * Dieses Runnable versucht taktweise mögliche Angekommende Pakete
 * der PASSIVE Klasse @ClientConnection vom Stream zu holen
 * und in das Model zu übertragen.
 * 
 * Die Klasse @ClientConnection stellt hierfür die methode
 * --- void void waitReceiveAndUpdateModel() throws IOException,
 * ClassNotFoundException, InterruptedException {}
 * 
 * zur Verfügung
 * 
 * Die Run-Methode wird so lange ausgeführt bis entweder
 * der THREADPOOL runtergefahren wird ODER das Runnable von Hand gestoppt wird
 * 
 * 
 * Ausgeführt wird das Runnable über den ThreadPool der Klasse @ClientController
 */

public class RunnableReceiveServerBroadcasts implements Runnable
{
	private ClientConnection connection;

	private static final int TAKT = 25;

	private boolean stopped = false;

	public RunnableReceiveServerBroadcasts(ClientConnection connection)
	{
		this.connection = connection;
	}

	public synchronized void stopRunning()
	{
		stopped = true;
	}

	public synchronized boolean isStopped()
	{
		return stopped;
	}

	@Override
	public void run()
	{

		while (!this.stopped)
		{
			try
			{
				Thread.sleep(TAKT);
				connection.tryReceiveAndUpdateModel();
			} catch (SocketException e)
			{
				System.out.println("RunnableReceiveServerBroadcasts: Socket closed, i terminate");
			}

			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}
}
