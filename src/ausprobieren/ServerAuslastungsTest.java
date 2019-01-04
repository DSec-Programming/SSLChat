package ausprobieren;

import java.security.Security;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import client.connection.ClientConnection2;
import client.connection.RunnableSendObject;
import client.model.ClientDataModel;
import streamedObjects.ClientHello;
import streamedObjects.MessageFromClient;

public class ServerAuslastungsTest
{
	private static final int numberOfClients = 20;

	private static int name = 1;

	public static void main(String[] args)
	{
		ArrayList<ClientConnection2> clientList = new ArrayList<ClientConnection2>();
		for (int i = 0; i < numberOfClients; i++)
		{
			clientList.add(init());
		}

		for (ClientConnection2 cc : clientList)
		{
			Thread t = new Thread(() ->
			{
				while(true)
				{
					cc.send(new MessageFromClient("Hallo"));
					try
					{
						Thread.sleep((long) (Math.random() * 5000) + 3000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}

	public static ClientConnection2 init()
	{
		ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
		Security.addProvider(new BouncyCastleProvider());

		try
		{
			Security.addProvider(new BouncyCastleJsseProvider());

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");

			TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			trustMgrFact.init(Utils.createServerTrustStore());

			sslContext.init(null, trustMgrFact.getTrustManagers(), null);

			SSLSocketFactory fact = sslContext.getSocketFactory();
			SSLSocket s = (SSLSocket) fact.createSocket("localhost", 44444); //port änderungen !! 

			ClientConnection2 connection = new ClientConnection2(s, new ClientDataModel());
			pool.submit(new RunnableSendObject(connection, new ClientHello("Peter" + name)));
			name++;
			return connection;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void send(ThreadPoolExecutor pool)
	{

	}
}
