package ausprobieren;

import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;



import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

public class BasicBCTLSServerWithClientAuth
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("StartServer");
		Security.addProvider(new BouncyCastleJsseProvider());

		SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
		kmf.init(Utils.createServerKeyStore(), Utils.SERVER_PASSWORD);
		
		//Für Client Auth auch noch eine TrustManagerFactroy
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX","BCJSSE");
		tmf.init(Utils.createClientTrustStore());


		sslContext.init(kmf.getKeyManagers(), null, null);

		SSLServerSocketFactory fact = sslContext.getServerSocketFactory();
		SSLServerSocket serverSocket = (SSLServerSocket) fact.createServerSocket(55555);
		
		//serverSocket.setNeedClientAuth(true);

		SSLSocket socket = (SSLSocket) serverSocket.accept();
		System.out.println("Have new Client: " + socket.getRemoteSocketAddress());

	}
}
