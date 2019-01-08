package server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.model.ConnectionModel;
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
	private ConnectionModel model;
	private ServerDataModel serverDataModel;

	public ServerSocketEntrace(int port, ConnectionModel model, ServerDataModel sdmodel) throws IOException
	{
		setDaemon(true);
		this.serverSocket = new ServerSocket(port);
		serverSocket.setReuseAddress(true);
		this.model = model;
		this.serverDataModel = sdmodel;
	}

	@Override
	public void run()
	{
		// so lange wie server nicht terminiert
		while (!isInterrupted())
		{
			try
			{
				Socket socketForClient = serverSocket.accept();
				serverDataModel.addNotification("(TCPServerSocket)>>> NEW CLIENT : " + socketForClient.getRemoteSocketAddress());
				SingleClientConnection2 connection = new SingleClientConnection2(socketForClient,
						this.model.getServerDataModel(), model);
				this.serverDataModel.addNotification("Create new (TCP) SingleClientConnection2");
				
			} catch (Exception e)
			{
				try
				{
					serverSocket.close();
					
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				interrupt();
				e.printStackTrace();
			}
		}

	}

}
