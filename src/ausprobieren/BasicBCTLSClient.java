package ausprobieren;

import java.security.Security;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;


public class BasicBCTLSClient
{
	public static void main(String[] args) throws Exception
	{
		Security.addProvider(new BouncyCastleJsseProvider());

		SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
		TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
		trustMgrFact.init(Utils.createServerTrustStore());
		sslContext.init(null, trustMgrFact.getTrustManagers(), null);
		SSLSocketFactory fact = sslContext.getSocketFactory();
		SSLSocket cSock = (SSLSocket) fact.createSocket("localhost", 55555);
		System.out.println("do create socket on localhost, 55555");
		cSock.close();
		System.out.println("close Socket");
	}
}
