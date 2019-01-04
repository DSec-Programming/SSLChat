package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkInfo
{	
	public static String getCurrentNetworkIp() {
		try(final DatagramSocket socket = new DatagramSocket())
		{
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		} 
		catch (SocketException | UnknownHostException e)
		{
			e.printStackTrace();
		}	
		return "127.0.0.1";
    }
}
