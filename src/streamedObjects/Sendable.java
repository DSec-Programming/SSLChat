package streamedObjects;

import java.io.Serializable;

public abstract class Sendable implements Serializable
{
	public static final long serialVersionUID = 0L;
	
	private long id;
	private long timeStamp;
	private boolean wantAnswer;
	
	public abstract long getID();
	public abstract boolean wantAnswer();
	public abstract long getTimeStamp();
}
