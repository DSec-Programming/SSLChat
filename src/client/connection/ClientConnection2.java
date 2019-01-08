package client.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import client.model.ClientDataModel;
import streamedObjects.ClientHello;
import streamedObjects.ClientSaysBye;
import streamedObjects.Sendable;

public class ClientConnection2
{
	private Socket s;
	private ReceiverThread receiver;
	private SenderThread sender;
	private SendList sendList;
	private ReceiveList receiveList;
	private ArrayList<Long> idList;

	public ClientConnection2(Socket s, ClientDataModel model) throws IOException
	{
		this.s = s;
		this.sendList = new SendList();
		this.receiveList = new ReceiveList();
		this.idList = new ArrayList<>();

		this.receiver = new ReceiverThread(s.getInputStream(), model, receiveList, idList);
		this.sender = new SenderThread(s.getOutputStream(), sendList, idList);

		this.receiver.start();
		this.sender.start();
	}

	public synchronized void send(Sendable s)
	{
		this.sendList.add(s);
	}

	public void stop() throws IOException
	{
		this.sendList.add(new ClientSaysBye());
		//!!!!!SCHÖNER!!!!!
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
