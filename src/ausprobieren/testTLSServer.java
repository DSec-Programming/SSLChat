package ausprobieren;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

import org.bouncycastle.tls.DefaultTlsServer;
import org.bouncycastle.tls.TlsServer;
import org.bouncycastle.tls.TlsServerProtocol;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class testTLSServer
{
	public static void main(String[] args) throws Exception
	{

		int port = 55555;
		ServerSocket ss = new ServerSocket(port);
		Socket s = ss.accept();

		
		TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
		TlsServer server = new DefaultTlsServer(crypto)
		{
			
			// Override e.g. TlsServer.getRSASignerCredentials() or
			// similar here, depending on what credentials you wish to use.
		};
		TlsServerProtocol protocol = new TlsServerProtocol(s.getInputStream(), s.getOutputStream());
		// Performs a TLS handshake
		protocol.accept(server);
		// Read/write to protocol.getInputStream(), protocol.getOutputStream()

		protocol.close();

	}
}
