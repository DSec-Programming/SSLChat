package server.connection;

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

public class SSLServerSocketEntrace extends Thread
{
	private SSLServerSocket serverSocket;
	private ConnectionModel model;

	public SSLServerSocketEntrace(int port, ConnectionModel model)
	{
		this.model = model;
		setDaemon(true);
		System.out.println("StartServer");
		Security.addProvider(new BouncyCastleJsseProvider());
		Security.addProvider(new BouncyCastleProvider());
		try
		{

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
			kmf.init(Utils.createServerKeyStore(), Utils.SERVER_PASSWORD);

			// Für Client Auth auch noch eine TrustManagerFactroy
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			tmf.init(Utils.createClientTrustStore());

			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			SSLServerSocketFactory fact = sslContext.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket) fact.createServerSocket(port);

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

		System.out.println("(SSLServerSocket)>>> waiting for clients ... ");
		// so lange wie server nicht terminiert
		while (true)
		{
			try
			{
				SSLSocket socketForClient = (SSLSocket) this.serverSocket.accept();
				System.out.println("(SSLServerSocket)>>> NEW CLIENT : " + socketForClient.getRemoteSocketAddress());
				
				SingleClientConnection2 connection = new SingleClientConnection2(socketForClient,
						this.model.getServerDataModel(), model);
				
				System.out.println("Create new (SSL) SingleClientConnection2");

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
