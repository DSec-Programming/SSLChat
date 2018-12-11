package ausprobieren;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.TlsClient;
import org.bouncycastle.tls.TlsClientProtocol;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class BasicBCTLSClientWithClientAuth
{
	public static void main(String[] args) throws Exception
	{
		try
		{

			Security.addProvider(new BouncyCastleJsseProvider());

			SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");
			kmf.init(Utils.createClientKeyStore(), Utils.CLIENT_PASSWORD);

			TrustManagerFactory trustMgrFact = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
			trustMgrFact.init(Utils.createServerTrustStore());

			sslContext.init(kmf.getKeyManagers(), trustMgrFact.getTrustManagers(), null);
			SSLSocketFactory fact = sslContext.getSocketFactory();

			SSLSocket cSock = (SSLSocket) fact.createSocket("143.93.55.138", 55555);

			TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
			TlsClient client = new DefaultTlsClient(crypto)
			{
				// MUST implement TlsClient.getAuthentication() here
				@Override
				public TlsAuthentication getAuthentication() throws IOException
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
			TlsClientProtocol protocol = new TlsClientProtocol(cSock.getInputStream(), cSock.getOutputStream());
			// Performs a TLS handshake
			protocol.connect(client);
			// Read/write to protocol.getInputStream(),
			// protocol.getOutputStream()

			System.out.println("skippe Senden/Empfangen");

			protocol.close();

			cSock.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
