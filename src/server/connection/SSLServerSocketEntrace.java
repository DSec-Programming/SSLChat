package server.connection;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import ausprobieren.Utils;
import server.model.ConnectionModel;
import server.model.ServerDataModel;

public class SSLServerSocketEntrace extends Thread
{
	private SSLServerSocket serverSocket;
	private ConnectionModel model;
	private ServerDataModel serverDataModel;
	
	private static final String serverpswd = "server1234";

	public SSLServerSocketEntrace(int port, ConnectionModel model,ServerDataModel sdmodel)
	{
		this.model = model;
		this.serverDataModel = sdmodel;
		setDaemon(true);
		Security.addProvider(new BouncyCastleJsseProvider());
		Security.addProvider(new BouncyCastleProvider());
		try
		{

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
			
			FileInputStream fis = new FileInputStream("src/javaCreatedServerKeyStore.jks");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(fis, serverpswd.toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
			
			kmf.init(ks, "server1234".toCharArray());
		
			
			sslContext.init(kmf.getKeyManagers(), null, null);
			
			SSLServerSocketFactory fact = sslContext.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket) fact.createServerSocket(port);
			serverSocket.setReuseAddress(true);

			serverSocket.setWantClientAuth(true);
			this.serverSocket = serverSocket;
			
		} catch (Exception e)
		{
			//ggf IO weiterschmeißen !! 
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		// so lange wie server nicht terminiert
		while (!isInterrupted())
		{
			try
			{
				SSLSocket socketForClient = (SSLSocket) this.serverSocket.accept();
				serverDataModel.addNotification("(SSLServerSocket)>>> NEW CLIENT : " + socketForClient.getRemoteSocketAddress());
				
				SingleClientConnection2 connection = new SingleClientConnection2(socketForClient,
						this.model.getServerDataModel(), model);
				
				this.serverDataModel.addNotification("Create new (SSL) SingleClientConnection2");

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
