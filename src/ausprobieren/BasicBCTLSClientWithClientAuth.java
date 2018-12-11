package ausprobieren;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;



public class BasicBCTLSClientWithClientAuth
{
	public static void main(String[] args) throws Exception
	{
		Security.addProvider(new BouncyCastleJsseProvider());

		SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
		kmf.init(Utils.createClientKeyStore(), Utils.CLIENT_PASSWORD);
		
		TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
		trustMgrFact.init(Utils.createServerTrustStore());
		
		sslContext.init(null, trustMgrFact.getTrustManagers(), null);
		SSLSocketFactory fact = sslContext.getSocketFactory();
		
		
		SSLSocket cSock = (SSLSocket) fact.createSocket("143.93.55.138", 55555);
		
		
		System.out.println("do create socket on localhost, 55555");
		cSock.close();
		System.out.println("close Socket");
	}
}
