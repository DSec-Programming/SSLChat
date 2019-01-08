package server.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocket;
import javax.swing.plaf.synth.SynthSpinnerUI;

import server.connection.CallableSendBroadcastUpdate;
import server.connection.SSLServerSocketEntrace;
import server.connection.ServerSocketEntrace;
import server.connection.SingleClientConnection2;
import streamedObjects.Kick;
import streamedObjects.ServerShutDown;

public class ConnectionModel
{
	private ServerSocketEntrace serverSocketEntrace;
	private SSLServerSocketEntrace sslServerSocketEntrace;
	private ThreadPoolExecutor pool;

	private ArrayList<SingleClientConnection2> openClientConnections;

	private ServerDataModel serverDataModel;
	private boolean loaded = false;

	private int tcpPort;
	private int tlsPort;

	public ConnectionModel(ServerDataModel model)
	{
		this.serverDataModel = model;
		this.pool = new ThreadPoolExecutor(8, 8, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		this.openClientConnections = new ArrayList<>();
		this.tcpPort = 55555;
		this.tlsPort = 44444;
	}

	public synchronized void startServer()
	{
		try
		{
			if (!loaded)
			{
				loaded = true;
				ServerSocketEntrace sse = new ServerSocketEntrace(this.tcpPort, this, serverDataModel);
				serverDataModel.addNotification("(TCPServerSocket)>>> waiting for clients ... ");
				SSLServerSocketEntrace SSLsse = new SSLServerSocketEntrace(this.tlsPort, this, serverDataModel);
				serverDataModel.addNotification("(SSLServerSocket)>>> waiting for clients ... ");
				setServerSocketEntrace(sse);
				setSSLServerSocketEntrace(SSLsse);
				sse.start();
				SSLsse.start();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void stopServer()
	{

		if (loaded)
		{
			this.serverSocketEntrace.interrupt();
			serverDataModel.addNotification("kill TCPServerSocket");
			this.sslServerSocketEntrace.interrupt();
			serverDataModel.addNotification("kill TLSServerSocket");
			
			for(SingleClientConnection2 c :this.openClientConnections)
			{
				try
				{
					c.stop(new ServerShutDown());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			serverDataModel.removeAllUserInOnlineList();
			
			this.openClientConnections.clear();
			serverDataModel.addNotification("clearClientConnections");
		}

	}

	public synchronized ServerDataModel getServerDataModel()
	{
		return this.serverDataModel;
	}

	public synchronized void setServerSocketEntrace(ServerSocketEntrace sse)
	{
		this.serverSocketEntrace = sse;
	}

	public synchronized void setSSLServerSocketEntrace(SSLServerSocketEntrace SSLsse)
	{
		String s; //Redundant zu oben ??? 

		this.sslServerSocketEntrace = SSLsse;
	}

	public synchronized void sendUpdate(SingleClientConnection2 scc, String type, ArrayList<String> update)
	{
		pool.submit(new CallableSendBroadcastUpdate(scc, type, update));
	}

	public int getTcpPort()
	{
		return tcpPort;
	}

	public void setTcpPort(int tcpPort)
	{
		this.tcpPort = tcpPort;
	}

	public int getTlsPort()
	{
		return tlsPort;
	}

	public void setTlsPort(int tlsPort)
	{
		this.tlsPort = tlsPort;
	}

	public boolean getLoaded()
	{
		return this.loaded;
	}

	public void setLoaded(boolean b)
	{
		this.loaded = b;
	}

	/**
	 * Wird bei der neuaufnahme eines Clients aufgerufen ! hier werden offene
	 * Verbindungen gespeichert
	 */
	public synchronized void addSingleClientConnection(SingleClientConnection2 scc)
	{
		this.openClientConnections.add(scc);
		// adde auch in OnlineList
		if (scc.getSocket() instanceof SSLSocket)
		{
			this.serverDataModel.addUserInOnlineList(scc.getUsername() + " [AUTHENTICATED]");
		} else
		{
			this.serverDataModel.addUserInOnlineList(scc.getUsername());
		}
		this.serverDataModel.addNotification(scc.getUsername() + " connected !");
	}

	public synchronized void removeSingleClientConnection(SingleClientConnection2 scc)
	{
		this.openClientConnections.remove(scc);
		// remove auch aus OnlineList
		if (scc.getSocket() instanceof SSLSocket)
		{
			this.serverDataModel.removeUserInOnlineList(scc.getUsername() + " [AUTHENTICATED]");
		} else
		{
			this.serverDataModel.removeUserInOnlineList(scc.getUsername());
		}
		this.serverDataModel.addNotification(scc.getUsername() + " logged out !");
	}

	public synchronized ArrayList<SingleClientConnection2> getAllOpenSingleClientConnections()
	{
		return this.openClientConnections;
	}

	public synchronized void kickUser(String user)
	{
		for (SingleClientConnection2 s : this.openClientConnections)
		{
			if (s.getUsername().equals(user))
			{
				s.send(new Kick());
			}
		}
	}
}
