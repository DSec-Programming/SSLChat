package ssl.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ssl.client.model.ClientDataModel;
import ssl.streamedObjects.UpdateFromServer;

public class ClientConnection
{
	private Socket socket;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	private ClientDataModel model;

	public static final String USER_UPDATE = "userupdate";
	public static final String CHAT_UPDATE = "chatupdate";

	public ClientConnection(String host, int port, String username, ClientDataModel model) throws IOException
	{
		this.socket = new Socket(host, port);
		this.toServer = new ObjectOutputStream(this.socket.getOutputStream());
		this.fromServer = new ObjectInputStream(this.socket.getInputStream());
		this.model = model;

		// Beim Server anmelden !
		this.send(username);
	}

	public void send(Object o) throws IOException
	{
		this.toServer.writeObject(o);
		this.toServer.flush();
	}

	public synchronized void waitReceiveAndUpdateModel()
			throws IOException, ClassNotFoundException, InterruptedException
	{
		if (this.socket.getInputStream().available() == 0)
		{
			return;
		}
		Object data = this.fromServer.readObject();

		// update model TODO
		// geht noch schöner und Dynamischer
		// hier jetzt er statisch ...
		if (data instanceof UpdateFromServer)
		{
			UpdateFromServer update = (UpdateFromServer) data;
			if (update.getUpdateType().equals(CHAT_UPDATE))
			{
				// chatUpdate
				this.model.updateMessageToChat(update.getUpDate());

			} else if (update.getUpdateType().equals(USER_UPDATE))
			{
				// userupdate
				this.model.updateUserInOnlineList(update.getUpDate());
			} else
			{
				System.out.println("RUNTIMEEXECEPTION");
				throw new RuntimeException("NICHT ERWARTETES OBJECT VOM SERVER");
			}
		}
	}

	public synchronized void close() throws IOException
	{
		this.socket.close();
	}

}
