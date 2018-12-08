package ausprobieren;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class FirstSSLclient
{
	public static void main(String[] args)
	{
		try
		{
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			System.out.println("have sslsocketfactory");
			SSLSocket sslSocket = (SSLSocket) sslsocketfactory.createSocket("localhost", 44344);
			System.out.println("have sslSocket");
			
			sslSocket.startHandshake();


			
			ObjectOutputStream oos = new ObjectOutputStream(sslSocket.getOutputStream());
			oos.writeObject(new String("Hallo"));
			oos.flush();
			
			System.out.println("Send to server");
			
			ObjectInputStream ois = new ObjectInputStream(sslSocket.getInputStream());
			String reply = (String) ois.readObject();
			System.out.println("serverReply: " + reply);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
