package client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import client.model.ClientDataModel;
import client.model.ConnectionModel;
import streamedObjects.Ping;
import streamedObjects.UpdateFromServer;

/**
 * Spezifikation
 * 
 * Die Klasse ClientConnection ist eine PASSIVE Klasse --> Sie wird von
 * verschiedenen Runnables als GEMEINSAMES Object benutzt -->synchronisieren!
 * 
 * Ein TreadPool steutert den Zugriff auf die Daten
 * 
 * RUNNABLES: - Runnable-SendObject - Runnable-ReceiveSerferBroadcasts
 * 
 * 
 */

public class ClientConnection
{
	private Socket tcpSocket;

	private ObjectOutputStream toServer;

	private ObjectInputStream fromServer;

	private ClientDataModel clientDataModel;

	private ConnectionModel connectionModel;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public static final String AUTHORIZED = "AUTHORIZED";
	public static final String UNAUTHORIZED = "UNAUTHORIZED";

	public static final String SSL = "SSL";
	public static final String TCP = "TCP";

	/**
	 * Der Kontruckter wird ...
	 * 
	 * UMBAUEN !! vielleicht Model nachtr�glich setzen als im Konstuktor
	 * mitzugeeeben
	 * 
	 */

	// TCP Construcktor
	public ClientConnection(Socket s, ClientDataModel clientConnectionModel, ConnectionModel connectionModel)
			throws IOException
	{
		this.tcpSocket = s;

		this.toServer = new ObjectOutputStream(this.tcpSocket.getOutputStream());
		this.fromServer = new ObjectInputStream(this.tcpSocket.getInputStream());
		this.clientDataModel = clientConnectionModel;
		this.connectionModel = connectionModel;

		// Alle wichtigen Informationen beim Model Eintragen
		// damit die Oberfl�che aktualisiert werden kann

		if (s instanceof SSLSocket)
		{

		} else
		{
			// Da hier ein TCPSocket vorliegt muss das Model
			// wie folgt aktualisiert werden
			this.connectionModel.setConnectionTyp("TCP");
			this.connectionModel.setServerStatus(UNAUTHORIZED);
			this.connectionModel.setClientStatus(UNAUTHORIZED);
		}
		// hiermit schlie�t der Socket automatisch
		// sollte das Programm terminieren
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				try
				{
					tcpSocket.close();
					System.out.println("The ClientSocket is shut down!");
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * schreibt ein Object auf den Stream und sendet in entg�ltig (sp�ht den
	 * Stream durch) ! Das �bergebene Object muss das INTERFACE Serilizable
	 * implementieren !
	 */
	public synchronized void send(Object o) throws IOException
	{
		this.toServer.writeObject(o);
		this.toServer.flush();
	}

	/**
	 * schaut nach ob etwas auf dem Stream liegt falls ja -> return sofort
	 * Ansonsten lie�t die Methode EIN Object vom Stream durch instanceof kann
	 * entschieden werden wie es Gecastet werden muss. Empfohlene Verbesserung:
	 * Benutzung INTERPRETER TODO
	 */

	public synchronized void tryReceiveAndUpdateModel() throws IOException, ClassNotFoundException, InterruptedException
	{
		if (this.tcpSocket.getInputStream().available() == 0)
		{
			return;
		}
		Object data = this.fromServer.readObject();
		// geht noch sch�ner und Dynamischer
		// hier jetzt ehr statisch ...

		if (data instanceof Ping)
		{
			this.toServer.writeObject(new Ping());
		} else if (data instanceof UpdateFromServer)
		{
			UpdateFromServer update = (UpdateFromServer) data;
			if (update.getUpdateType().equals(CHAT_UPDATE))
			{
				// chatUpdate
				this.clientDataModel.updateMessageToChat(update.getUpDate());

			} else if (update.getUpdateType().equals(USER_UPDATE))
			{
				// userupdate
				this.clientDataModel.updateUserInOnlineList(update.getUpDate());
			} else
			{
				System.out.println("RUNTIMEEXECEPTION");
				throw new RuntimeException("NICHT ERWARTETES OBJECT VOM SERVER");
			}
		}
	}

	/*
	 * schlie�t den Socket
	 */
	public synchronized void close() throws IOException
	{
		this.tcpSocket.close();
	}

}
