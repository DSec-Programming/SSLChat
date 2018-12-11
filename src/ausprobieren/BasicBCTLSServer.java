package ausprobieren;

import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;



public class BasicBCTLSServer {
	public static void main(String[] args) throws Exception {
		System.out.println("StartServer");
		Security.addProvider(new BouncyCastleJsseProvider());

		SSLContext sslContext = SSLContext.getInstance("TLS", "BCJSSE");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX", "BCJSSE");

		kmf.init(Utils.createServerKeyStore(), Utils.SERVER_PASSWORD);

		sslContext.init(kmf.getKeyManagers(), null, null);

		SSLServerSocketFactory fact = sslContext.getServerSocketFactory();
		SSLServerSocket serverSocket = (SSLServerSocket) fact.createServerSocket(55555);

		SSLSocket socket = (SSLSocket) serverSocket.accept();

		System.out.println("Have new Client: " + socket.getRemoteSocketAddress());

		//TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
		
		TlsServerProtocol protocol = new TlsServerProtocol(socket.getInputStream(), socket.getOutputStream(),new SecureRandom());
		// Performs a TLS handshake
		//protocol.accept(server);
		protocol.accept(new MockTlsServer());
		// Read/write to protocol.getInputStream(), protocol.getOutputStream()

		protocol.close();

	}

}
