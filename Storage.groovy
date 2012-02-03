// handles stack of menu names, both push and pop;
// base menu is always the lowest entry on stack and cannot be removed
class Storage
{
	List<String> stackMenu = []
	boolean audit = false
	def mainmenufilename = "./data/main.txt"

	// print debug text (maybe)
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// class constructor - loads configuration,etc.
	public Storage()
	{
		stackMenu << mainmenufilename
	} /// end of constructor

	// test harness for this class
	public static void main(String[] args)
	{	
		Storage s = new Storage()	// started with main.txt on stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"
		println "--------------------------\n"

		s.leftShift("bill")		// added bill to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"
		println "--------------------------\n"

		s.leftShift("eve")		// added fred to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("fred")		// added fred to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		s.leftShift("jim")		// added fred to stack
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n\n"
		println "--------------------------\n"

		println "starting F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"

		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"
		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred
		println "--------------------------\n"
		println "         F12 --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		s.pop()				// popped fred


		println "===================================="


		println "starting to pop() --------------"
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		s.pop()				// popped fred
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"

		s.pop()				// popped bill
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"

		s.pop()				// should not erase main.txt (the first stack entry)
		println "PriorMenu=${s.getPriorMenu()}"
		println "CurrentMenu=${s.getCurrentMenu()}"
		//println "stack at ${s.get()}\n"
	}

	// non-distructive retrieve prior menu file name
	public getPriorMenu()
	{	
		def value = null
		if (stackMenu.size()>1)
		{
			value = stackMenu[stackMenu.size()-2]
		}
		else
		{
			value = stackMenu[0]
		} // end of if
		return value
	} // end of getPriorMenu

	// non-distructive retrieve prior menu file name
	public getCurrentMenu()
	{	
		def value = null
		if (stackMenu.size()>0)
		{
			value = stackMenu[stackMenu.size()-1]
		}
		else
		{
			value = stackMenu[0]
		} // end of if
		return value
	} // end of getCurrentMenu

	// the << overload operator
	synchronized void leftShift(value)
	{
		if(audit) say "stackMenu.size() before push is ${stackMenu.size()}"
		stackMenu << value
		if(audit) say "stackMenu.size() after push is ${stackMenu.size()}    \nstorage.pushed: $value at ${stackMenu.size()-1}"
		notifyAll()
	} // end of left...

	// 
	synchronized Object pop()
	{
		while (!stackMenu)
		{
			try{ wait() }
			catch(InterruptedException e) {}
		} // end of while

		def value = null
		if(audit) say "will storage.pop: stackMenu.size()= ${stackMenu.size()} but -1 is ${stackMenu.size()-1}"

		if (stackMenu.size()>1)
		{
			value = stackMenu.pop()
		    
		}
		else
		{
			value = stackMenu[0]
		} // end of if

		if(audit) say "storage.popped: $value at ${stackMenu.size()-1}"
		return value
	} // end of method

	synchronized Object get()
	{
		while (!stackMenu)
		{
			try{ wait() }
			catch(InterruptedException e) {}
		} // end of while

		def value = null
		if (stackMenu.size()>0)
		{
			value = stackMenu[stackMenu.size()-1]
		} // end of if
		if(audit) say "storage.get: $value at ${stackMenu.size()-1} and stackMenu.size() is ${stackMenu.size()}"
		return value
	} // end of method

} // end of class