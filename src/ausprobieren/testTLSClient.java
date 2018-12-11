package ausprobieren;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;

import org.bouncycastle.tls.TlsClientProtocol;
import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.TlsClient;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class testTLSClient
{
	public static void main(String[] args) throws Exception
	{
		// TlsCrypto to support client functionality

		InetAddress address = InetAddress.getByName("143.93.55.138");
		int port = 443;
		Socket s = new Socket(address, port);
		
		
		
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
		TlsClientProtocol protocol = new TlsClientProtocol(s.getInputStream(), s.getOutputStream());
		// Performs a TLS handshake
		protocol.connect(client);
		// Read/write to protocol.getInputStream(), protocol.getOutputStream()

		protocol.close();

	}
}
