package ausprobieren;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SecondSSLserver
{
	public static void main(String[] args)
	{
		try
		{
			
			//https://blog.trifork.com/2009/11/10/securing-connections-with-tls/
			
			
			
			
			InputStream keyStoreResource = new FileInputStream("testkeystore.ks");
			char[] keyStorePassphrase = "testpwd".toCharArray();
			KeyStore ksKeys = KeyStore.getInstance("JKS");
			ksKeys.load(keyStoreResource, keyStorePassphrase);
			System.out.println("load keystore ... ");

			// KeyManager decides which key material to use.

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ksKeys, keyStorePassphrase);
			System.out.println("init KeyManagerFactory ...");

			InputStream trustStoreIS = new FileInputStream("myTrustStore");
			char[] trustStorePassphrase = "testpwd".toCharArray();
			KeyStore ksTrust = KeyStore.getInstance("JKS");
			ksTrust.load(trustStoreIS, trustStorePassphrase);
			System.out.println("load Truststore ... ");

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ksTrust);
			System.out.println("init TrustManagerFactory");

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			System.out.println("init SSLContext(KeyManagerFactory,TrustManagerFactory)) ...");

			SSLSocketFactory sf = sslContext.getSocketFactory();
			System.out.println("instanciate SSLSocketFactory ...");

			
			
	
			s.setEnabledProtocols(SslConstants.intersection(s.getSupportedProtocols(), StrongSsl.ENABLED_PROTOCOLS));
			s.setEnabledCipherSuites(
					SslConstants.intersection(s.getSupportedCipherSuites(), StrongSsl.ENABLED_CIPHER_SUITES));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
