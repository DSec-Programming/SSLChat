package main;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.x500.X500PrivateCredential;

public class KeyStoreLoadThread extends Thread
{

	@Override
	public void run()
	{
		try
		{
			String alias = "server";
			String serverpswd = "server1234";

			//ist das verzeichnis wo dir jar datei gestartet wird
			//String path = System.getProperty("usr.dir");

			FileInputStream fis = new FileInputStream("src/serverKeyStore.jks");

			FileInputStream fis2 = new FileInputStream("src/ServerCert.pem");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection c = cf.generateCertificates(fis2);
			Iterator i = c.iterator();
			
			Certificate cert = null;
			while (i.hasNext())
			{
				cert = (Certificate) i.next();
				System.out.println(cert);
			}

			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(fis, serverpswd.toCharArray());

			Key key = ks.getKey(alias, serverpswd.toCharArray());

			KeyPair pair = null;


			if (key instanceof PrivateKey)
			{
				cert = ks.getCertificate(alias);
				
				PublicKey pubkey = cert.getPublicKey();
				pair = new KeyPair(pubkey, (PrivateKey) key);
			}

			X509Certificate rootCert = (X509Certificate) cert;

			X500PrivateCredential serverCred = new X500PrivateCredential(rootCert, pair.getPrivate(), "");


			ks.setKeyEntry(serverCred.getAlias(), serverCred.getPrivateKey(), serverpswd.toCharArray(),
					new X509Certificate[]
					{ rootCert });
			
			

			System.out.println("DONE");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
