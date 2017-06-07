public class MenuCounter
{
	private MenuCounter() {}
	private static menucounter = 0;
	public static int getKeyCounter()
	{
		menucounter+=1;
	}

	public static void main(String[] args)
	{	
		assert(1==MenuCounter.getKeyCounter())
		assert(2==MenuCounter.getKeyCounter())
	}
}