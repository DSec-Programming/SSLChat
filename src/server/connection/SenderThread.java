package server.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import streamedObjects.Sendable;

public class SenderThread extends Thread
{
	private ObjectOutputStream oos;
	private SendList sendList;
	private ArrayList<Long> idList;

	public SenderThread(OutputStream os, SendList sendList, ArrayList<Long> idList) throws IOException
	{
		this.oos = new ObjectOutputStream(os);
		this.sendList = sendList;
		this.idList = idList;
	}
	
	@Override
	public void run()
	{
		try
		{
			while(!isInterrupted())
			{
				Sendable s = this.sendList.get();
				if(s.wantAnswer())
				{
					this.idList.add(s.getID());
				}
				
				this.oos.writeObject(s);
				oos.flush();
			}
			
			this.oos.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
