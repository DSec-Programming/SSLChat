package streamedObjects;


public abstract class Sendable
{
	private long id;
	private long timeStamp;
	private boolean wantAnswer;
	
	public abstract long getID();
	public abstract boolean wantAnswer();
	public abstract long getTimeStamp();
}
