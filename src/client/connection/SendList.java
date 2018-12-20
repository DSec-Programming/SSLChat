package client.connection;

import java.util.ArrayList;

import streamedObjects.Sendable;

public class SendList
{
	private ArrayList<Sendable> list;

	public SendList()
	{
		this.list = new ArrayList<>();
	}

	public synchronized void add(Sendable s)
	{
		this.list.add(s);
		notifyAll();
	}

	public synchronized Sendable get() throws InterruptedException
	{
		while (this.list.size() == 0)
		{
			wait();
		}
		Sendable temp = this.list.get(0);
		this.list.remove(temp);
		return temp;

	}
}