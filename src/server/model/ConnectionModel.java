package server.model;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import server.connection.CallableSendBroadcastUpdate;
import server.connection.RunnableObserveSingleClientConnections;
import server.connection.RunnableRemoveInactivesClients;
import server.connection.ServerSocketEntrace;
import server.connection.SingleClientConnection;

public class ConnectionModel
{
	private ServerSocketEntrace serverSocketEntrace;
	private ThreadPoolExecutor pool;

	private ArrayList<SingleClientConnection> openClientConnections;

	private ServerDataModel serverDataModel;

	public ConnectionModel(ServerDataModel model)
	{
		this.serverDataModel = model;
		this.pool = new ThreadPoolExecutor(4, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		this.openClientConnections = new ArrayList<>();
	}

	public synchronized ServerDataModel getServerDataModel()
	{
		return this.serverDataModel;
	}

	public synchronized void setServerSocketEntrace(ServerSocketEntrace sse)
	{
		this.serverSocketEntrace = sse;
		this.pool.submit(new RunnableObserveSingleClientConnections(this, this.pool));
		//this.pool.submit(new RunnableRemoveInactivesClients(this));

	}

	public synchronized void sendUpdate(SingleClientConnection scc, String type, ArrayList<String> update)
	{
		pool.submit(new CallableSendBroadcastUpdate(scc, type, update));
	}

	/**
	 * Wird bei der neuaufnahme eines Clients aufgerufen ! hier werden offene
	 * Verbindungen gespeichert
	 */
	public synchronized void addSingleClientConnection(SingleClientConnection scc)
	{
		this.openClientConnections.add(scc);
		// adde auch in OnlineList
		this.serverDataModel.addUserInOnlineList(scc.getUserName());
	}

	public synchronized void removeSingleClientConnection(SingleClientConnection scc)
	{
		this.openClientConnections.remove(scc);
		// remove auch aus OnlineList
		this.serverDataModel.removeUserInOnlineList(scc.getUserName());
	}

	public synchronized ArrayList<SingleClientConnection> getAllOpenSingleClientConnections()
	{
		return this.openClientConnections;
	}
}
