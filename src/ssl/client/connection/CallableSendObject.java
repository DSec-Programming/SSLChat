package ssl.client.connection;

import java.util.concurrent.Callable;

public class CallableSendObject implements Callable<Boolean>
{

	private ClientConnection connection;
	private Object toSend;

	public CallableSendObject(ClientConnection connection, Object o)
	{
		// TODO Auto-generated constructor stub
		this.connection = connection;
		this.toSend = o;
	}

	@Override
	public Boolean call() throws Exception
	{
		this.connection.send(this.toSend);
		return true;
	}
}
