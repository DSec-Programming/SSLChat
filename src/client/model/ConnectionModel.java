package client.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Security;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import ausprobieren.Utils;
import client.connection.ClientConnection2;
import client.connection.RunnableSendObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import streamedObjects.ClientHello;
import streamedObjects.MessageFromClient;

public class ConnectionModel
{
	private User user;
	private ClientConnection2 connection;

	private ThreadPoolExecutor pool;

	private StringProperty ServerIP;
	private StringProperty usedProvider;

	// ConnectionType e { "SSL","TCP" }
	private StringProperty connectionType;
	// ServerStatus e {"authorized","unauthorized"}
	private StringProperty serverStatus;
	// ClientStatus e {"authorized","unauthorized"}
	private StringProperty clientStatus;

	private int tcpPort;
	private int tlsPort;

	public ConnectionModel()
	{
		this.user = null;
		this.connection = null;

		this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		this.ServerIP = new SimpleStringProperty();
		this.usedProvider = new SimpleStringProperty();

		this.connectionType = new SimpleStringProperty();
		this.serverStatus = new SimpleStringProperty();
		this.clientStatus = new SimpleStringProperty();

		this.tcpPort = 55555;
		this.tlsPort = 44444;
	}

	public synchronized void openSocket(String ip, ClientDataModel clientDataModel) throws IOException
	{
		Socket s = new Socket(ip, this.tcpPort);
		ClientConnection2 connection = new ClientConnection2(s, clientDataModel);
		this.connection = connection;
		//Hallo sagen
		this.pool.submit(new RunnableSendObject(this.connection, new ClientHello(user.getUsername())));

	}

	public synchronized void openSSLSocket(String ip, ClientDataModel clientDataModel)
			throws IOException, ConnectException
	{
		Security.addProvider(new BouncyCastleProvider());
		try
		{
			Security.addProvider(new BouncyCastleJsseProvider());

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");

			String ss;
			//trustMgrFact.init(Utils.createServerTrustStore());
			
			FileInputStream fis = new FileInputStream("src/javaCreatedServerTrustStore.jks");
			KeyStore ts = KeyStore.getInstance("JKS");
			ts.load(fis, "server1234".toCharArray());
			
			TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			trustMgrFact.init(ts);
			
			sslContext.init(null, trustMgrFact.getTrustManagers(), null);

			SSLSocketFactory fact = sslContext.getSocketFactory();
			SSLSocket s = (SSLSocket) fact.createSocket(ip, this.tlsPort); //port änderungen !! 

			ClientConnection2 connection = new ClientConnection2(s, clientDataModel);
			this.connection = connection;
			this.pool.submit(new RunnableSendObject(this.connection, new ClientHello(user.getUsername())));

		} catch (Exception e)
		{
			/*
			 * Um anzudeuten, dass der Server nicht online ist
			 */
			if (e instanceof ConnectException)
			{
				throw new ConnectException();
			}
		}
	}
	// GETTER

	public synchronized User getUser()
	{
		return user;
	}

	public synchronized StringProperty getServerIP()
	{
		return ServerIP;
	}

	public synchronized StringProperty getUsedProvider()
	{
		return usedProvider;
	}

	public synchronized StringProperty getConnectionType()
	{
		return connectionType;
	}

	public synchronized StringProperty getServerStatus()
	{
		return serverStatus;
	}

	public synchronized StringProperty getClientStatus()
	{
		return clientStatus;
	}

	public int getTcpPort()
	{
		return tcpPort;
	}

	public int getTlsPort()
	{
		return tlsPort;
	}

	// SETTER

	public synchronized void setUser(User user)
	{
		this.user = user;
	}

	public synchronized void setConnectionTyp(String ct)
	{
		this.connectionType.set(ct);
	}

	public synchronized void setServerStatus(String status)
	{
		this.serverStatus.set(status);
	}

	public synchronized void setClientStatus(String status)
	{
		this.clientStatus.set(status);
	}

	public synchronized void sendMessageOverClientConnection(MessageFromClient msg)
	{
		this.pool.submit(new RunnableSendObject(this.connection, msg));
	}

	public synchronized void killConnectoin() throws IOException
	{
		if (this.connection != null)
		{
			this.connection.stop();
		}
		this.connection = null;
	}

	public synchronized void shutdownThreadPool()
	{
		this.pool.shutdown();
	}

	public void setTcpPort(int tcpPort)
	{
		this.tcpPort = tcpPort;
	}

	public void setTlsPort(int tlsPort)
	{
		this.tlsPort = tlsPort;
	}

}
