package server.model;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import server.connection.CallableSendBroadcastUpdate;
import server.connection.SSLServerSocketEntrace;
import server.connection.ServerSocketEntrace;
import server.connection.SingleClientConnection2;

public class ConnectionModel
{
	private ServerSocketEntrace serverSocketEntrace;
	private SSLServerSocketEntrace sslServerSocketEntrace;
	private ThreadPoolExecutor pool;

	private ArrayList<SingleClientConnection2> openClientConnections;

	private ServerDataModel serverDataModel;

	public ConnectionModel(ServerDataModel model)
	{
		this.serverDataModel = model;
		this.pool = new ThreadPoolExecutor(8, 8, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		this.openClientConnections = new ArrayList<>();
	}

	public synchronized ServerDataModel getServerDataModel()
	{
		return this.serverDataModel;
	}

	public synchronized void setServerSocketEntrace(ServerSocketEntrace sse)
	{
		this.serverSocketEntrace = sse;
		//this.pool.submit(new RunnableObserveSingleClientConnections(this, this.pool));
		// this.pool.submit(new RunnableRemoveInactivesClients(this));
	}
	
	public synchronized void setSSLServerSocketEntrace(SSLServerSocketEntrace SSLsse)
	{
		String s; //Redundant zu oben ??? 
		
		this.sslServerSocketEntrace = SSLsse;
		//this.pool.submit(new RunnableObserveSingleClientConnections(this, this.pool));
		// this.pool.submit(new RunnableRemoveInactivesClients(this));
	}

	public synchronized void sendUpdate(SingleClientConnection2 scc, String type, ArrayList<String> update)
	{
		pool.submit(new CallableSendBroadcastUpdate(scc, type, update));
	}

	/**
	 * Wird bei der neuaufnahme eines Clients aufgerufen ! hier werden offene
	 * Verbindungen gespeichert
	 */
	public synchronized void addSingleClientConnection(SingleClientConnection2 scc)
	{
		this.openClientConnections.add(scc);
		// adde auch in OnlineList
		
		this.serverDataModel.addUserInOnlineList(scc.getUsername());
	}

	public synchronized void removeSingleClientConnection(SingleClientConnection2 scc)
	{
		this.openClientConnections.remove(scc);
		// remove auch aus OnlineList
		this.serverDataModel.removeUserInOnlineList(scc.getUsername());
	}

	public synchronized ArrayList<SingleClientConnection2> getAllOpenSingleClientConnections()
	{
		return this.openClientConnections;
	}
}
