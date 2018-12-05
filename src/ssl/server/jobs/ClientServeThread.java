package ssl.server.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import ssl.server.model.ServerDataModel;

public class ClientServeThread extends Thread
{
	private Socket socket;
	private BufferedReader fromClient;
	// private BufferedWriter toClient;

	private ServerDataModel model;
	private String username;

	private static final String COMMAND_SENDONLINEUSERS = "sendonlineusers";
	private static final String COMMAND_SENDCHAT = "sendchat";

	public ClientServeThread(Socket socket, ServerDataModel model)
	{
	    setDaemon(true);
		this.model = model;
		try
		{
			this.socket = socket;
			this.fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// this.toClient = new BufferedWriter(new
			// OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// füge "USER"-name zur online liste hin
		try
		{
			this.username = fromClient.readLine();
			this.setName(this.username);

			this.model.addUserInOnlineList(this.username);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void run()
	{
		System.out.println("(" + this.getName() + ")>>>: has start");
		while (!socket.isClosed())
		{
			try
			{
				String request = fromClient.readLine();
				// NULL means Client has closed the connection
				if (request != null)
				{
					if (request.equals(COMMAND_SENDCHAT))
					{
						ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
						oos.writeObject(model.getChatMessages());
						oos.flush();

					} else if (request.equals(COMMAND_SENDONLINEUSERS))
					{
						ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
						oos.writeObject(model.getUserOnlineList());
						oos.flush();
					}

					// client send a message
					else
					{
						String msg = request;
						model.addMsgAtChat(this.getName() + ": " + msg);
						System.out.println("(" + this.getName() + ")>>>: client send: " + request);
					}
				} else
				{
					break;
				}

			} catch (SocketException e)
			{
				System.out.println("(" + this.getName() + ")>>>: USER QUIT CONNECTION ");
				break;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// Wenn der Client terminiert
		// Trage ihn aus der "online"-liste aus!
		model.removeUserInOnlineList(this.username);

		System.out.println("(" + this.getName() + ")>>>: TERMINATED ");

	}
}
