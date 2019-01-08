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
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
			//kmf.init(Utils.createServerKeyStore(), Utils.SERVER_PASSWORD);
			
			FileInputStream fis = new FileInputStream("src/server-keystore.jks");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(fis, serverpswd.toCharArray());
			
			kmf.init(ks, serverpswd.toCharArray());

			// Für Client Auth auch noch eine TrustManagerFactroy
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			tmf.init(Utils.createClientTrustStore());
			
			FileInputStream fis2 = new FileInputStream("src/server-keystore.jks");
			KeyStore ks2 = KeyStore.getInstance("JKS");
			ks2.load(fis2, serverpswd.toCharArray());

			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

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
