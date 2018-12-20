package server.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import server.model.ConnectionModel;
import server.model.ServerDataModel;
import streamedObjects.ClientSaysBye;
import streamedObjects.UpdateFromServer;

public class SingleClientConnection2
{
	private Socket s;
	private ReceiverThread receiver;
	private SenderThread sender;
	private SendList sendList;
	private ReceiveList receiveList;
	private ArrayList<Long> idList;
	private String username;

	public SingleClientConnection2(Socket s, ServerDataModel model, ConnectionModel cmodel) throws IOException
	{
		System.out.println("begin Konstruktor SingleCLientConnection2");
		this.s = s;
		this.sendList = new SendList();
		this.receiveList = new ReceiveList();
		this.idList = new ArrayList<>();

		System.out.println("Passive klassen init ... fertig sender/reveicerList");
		
		this.sender = new SenderThread(s.getOutputStream(), sendList, idList);
		System.out.println("init sender thread");
		this.receiver = new ReceiverThread(this, s.getInputStream(), model, cmodel, receiveList, idList);
		System.out.println("init receiver thread");
		System.out.println("init all Threads");
		
		this.receiver.start();
		this.sender.start();

	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUserName(String u)
	{
		this.username = u;
		
	}

	/**
	 * sendet ein update an DIESEN Client
	 **/
	public synchronized void sendUpdate(String updateType, ArrayList<String> update) throws IOException
	{
		this.sendList.add(new UpdateFromServer(updateType, update));
	}

	public void stop() throws IOException
	{
		this.sendList.add(new ClientSaysBye());
		//!!!!!SCH�NER!!!!!
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		this.sender.interrupt();
		this.receiver.interrupt();
		this.idList = null;
		this.sendList = null;
		this.receiveList = null;
		this.s.close();
	}
}
