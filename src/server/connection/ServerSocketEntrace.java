package server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.model.ServerDataModel;

/**
 * Spezifikation
 * 
 * ServerSocketEntrace ist ein EIGENER THREAD ### SINNVOLL?
 * --> vielleicht auch lieber als PassiveKlasse und dann in den ThreadPool
 * 
 * Die Klasse öffnet ein ServerSocket auf der Maschine.
 * 
 * Der ServerSocket akzeptiert neue Clients und speichert
 * deren Socket in der PASSIVEN Klasse SingleClientConnection
 * 
 * Das SingleClientConnection Object wird im Model gespeichert.
 * 
 * !!! DER CONTROLLER verwaltet den ThreadPOOL, welcher
 * REQUESTS, REPLEYS & BROADCASTS an die SingleClientConnections
 * verteilt/annimmt !!!
 * 
 * 
 */

public class ServerSocketEntrace extends Thread
{
	private ServerSocket serverSocket;
	private ServerDataModel model;

	public ServerSocketEntrace(int port, ServerDataModel model) throws IOException
	{
		setDaemon(true);
		System.out.println("=============== SSLServer v01 ===============");
		System.out.println("open ServerSocket...");
		this.serverSocket = new ServerSocket(port);
		this.model = model;
	}

	@Override
	public void run()
	{
		System.out.println("init ServerDataModel ...");

		System.out.println("(TCPServerSocket)>>> waiting for clients ... ");
		// so lange wie server nicht terminiert
		while (true)
		{
			try
			{
				Socket socketForClient = serverSocket.accept();
				System.out.println("(TCPServerSocket)>>> NEW CLIENT : " + socketForClient.getRemoteSocketAddress());
				SingleClientConnection connection = new SingleClientConnection(socketForClient, this.model);
				this.model.addSingleClientConnection(connection);
				//schicke dem neuen User gleich den aktuellen Chat !!
				

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

}
