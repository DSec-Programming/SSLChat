package ausprobieren;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.security.Security;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.net.ssl.internal.ssl.Provider;

public class FirstSSLserver
{
	public static void main(String[] args)
	{
		int sslPort = 44344;

		{
			// Registering the JSSE provider
			Security.addProvider(new Provider());

			// Specifying the Keystore details
			System.setProperty("javax.net.ssl.keyStore", "testkeystore.ks");
			System.setProperty("javax.net.ssl.keyStorePassword", "testpwd");

			// Enable debugging to view the handshake and communication which
			// happens between the SSLClient and the SSLServer
			// System.setProperty("javax.net.debug","all");
		}

		try
		{

			
			System.out.println("CUT ============================ ");
			

			// Initialize the Server Socket
			SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			System.out.println("have a SSLServerSocketFactory");
			SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(sslPort);
			System.out.println("have a sslServerSocket");
			while (true)
			{
				System.out.println("waiting for clients");
				SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
				System.out.println("NEW CLIENT: " + sslSocket.getRemoteSocketAddress());

				ObjectInputStream ois = new ObjectInputStream(sslSocket.getInputStream());
				String request = (String) ois.readObject();
				System.out.println(request);
				String reply = "'" + request + "'";
				ObjectOutputStream oos = new ObjectOutputStream(sslSocket.getOutputStream());
				oos.writeObject(reply);
				oos.flush();
				System.out.println("close Socket");

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
