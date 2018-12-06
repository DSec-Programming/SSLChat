package ssl.client.connection;

import java.io.IOException;

/**
 * Spezifikation:
 * 
 * Dieses Runnable versucht taktweise m�gliche Angekommende Pakete
 * der PASSIVE Klasse @ClientConnection vom Stream zu holen
 * und in das Model zu �bertragen.
 * 
 * Die Klasse @ClientConnection stellt hierf�r die methode
 * --- void void waitReceiveAndUpdateModel() throws IOException,
 * ClassNotFoundException, InterruptedException {}
 * 
 * zur Verf�gung
 * 
 * Die Run-Methode wird so lange ausgef�hrt bis entweder
 * der THREADPOOL runtergefahren wird ODER das Runnable von Hand gestoppt wird
 * 
 * 
 * Ausgef�hrt wird das Runnable �ber den ThreadPool der Klasse @ClientController
 */

public class RunnableReceiveServerBroadcasts implements Runnable
{
	private ClientConnection connection;

	private static final int TAKT = 50;

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
				connection.waitReceiveAndUpdateModel();
			}

			catch (InterruptedException e)
			{
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
