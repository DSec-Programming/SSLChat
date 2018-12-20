package server.connection;

import java.util.ArrayList;

import streamedObjects.Sendable;

public class ReceiveList
{
	private ArrayList<Sendable> list;
	
	public ReceiveList()
	{
		this.list = new ArrayList<>();
	}
	
	public synchronized void add(Sendable s)
	{
		this.list.add(s);
		notifyAll();
	}

	public synchronized Sendable get(long id) throws InterruptedException
	{
		while (this.list.size() == 0 || this.list.get(0).getID() != id)
		{
			wait();
		}
		for(Sendable s : this.list)
		{
			if(s.getID() == id)
			{
				return s;
			}
		}
		return null;

	}
}
