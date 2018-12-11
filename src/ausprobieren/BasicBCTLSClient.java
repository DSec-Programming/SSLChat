package ausprobieren;

import java.io.BufferedWriter;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Security;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

public class BasicBCTLSClient
{
	public static void main(String[] args)
	{
		try
		{

			Security.addProvider(new BouncyCastleJsseProvider());

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
			TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			trustMgrFact.init(Utils.createServerTrustStore());
			sslContext.init(null, trustMgrFact.getTrustManagers(), null);
			SSLSocketFactory fact = sslContext.getSocketFactory();
			SSLSocket cSock = (SSLSocket) fact.createSocket("143.93.55.138", 55555);
			System.out.println("do create socket on localhost, 55555");

			OutputStream os = cSock.getOutputStream();
			System.out.println("a");
			ObjectOutputStream oos = new ObjectOutputStream(os);
			System.out.println("b");
			oos.writeObject(new String("TEST"));
			System.out.println("c");
			oos.flush();
			System.out.println("d");

			cSock.close();
			System.out.println("close Socket");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
