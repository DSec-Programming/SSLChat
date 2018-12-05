package ssl.server.jobs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ssl.server.model.ServerDataModel;

public class ServerConnection extends Thread
{
	private ServerSocket serverSocket;
	private ServerDataModel model;
	
	public ServerConnection(int port,ServerDataModel model) throws IOException
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
				ClientServeThread cst = new ClientServeThread(socketForClient, this.model);
				cst.start();
				System.out.println("(SERVERSOCKET)>>> waiting for clients");

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

}
