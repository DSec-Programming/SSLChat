package ausprobieren;

public class KillThreadExample
{

	public static void main(String[] args)
	{

		// CountThread
		Thread ct = new Thread(() ->
		{
			try
			{
				int counter = 0;
				while (counter < 20)
				{
					Thread.sleep(1000);
					System.out.println("sleep 1sec ... (" + ++counter + "x)");
				}
			} catch (InterruptedException e)
			{
				System.out.println(e.getMessage());
			}

		});

		// KillThread
		Thread kt = new Thread(() ->
		{
			try
			{
				Thread.sleep(5000);
				
			} catch (InterruptedException e)
			{
				System.out.println(e.getMessage());
			}
		});

	}
}
