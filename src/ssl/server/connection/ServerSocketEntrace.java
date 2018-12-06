package ssl.server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ssl.server.model.ServerDataModel;

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

		System.out.println("(SERVERSOCKET)>>> waiting for clients ... ");
		// so lange wie server nicht terminiert
		while (true)
		{
			try
			{
				Socket socketForClient = serverSocket.accept();
				System.out.println("(SERVERSOCKET)>>> NEW CLIENT : " + socketForClient.getRemoteSocketAddress());
				SingleClientConnection connection = new SingleClientConnection(socketForClient, this.model);
				this.model.addSingleClientConnection(connection);
				System.out.println("(SERVERSOCKET)>>> waiting for clients");

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

}
