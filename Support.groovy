import javax.swing.*
import java.awt.*
import javax.swing.text.*;
import java.awt.BorderLayout
import javax.swing.BorderFactory; 
import javax.swing.border.*
import javax.swing.border.LineBorder
import java.awt.GridLayout
import groovy.swing.SwingBuilder

public class Support
{
	def static audit = false
	def static framefixedtitle = "MENU"

	def ls = System.getProperty('line.separator')
	def fs = System.getProperty('file.separator')
	def us = System.getProperty('user.name')
	def os = System.getProperty('os.name')
	Properties props
	def pwd=""

	def static int wide
	def static int high
	def static int top = 0
	def static int left = 0
	def frame

	Tracer problem = new Tracer();
	String propertyfile = '../menudata/menu.properties'  // non-OS specific parameters for business issues
	String pathfile = '../menudata/path.properties'  // non-OS specific parameters for business issues
	def config
	def paths
	def OSN 	// operating system name
	JTextPane jtp;			// jtextpane to show
	StyledDocument doc;
	SimpleAttributeSet as0
	SimpleAttributeSet as1
	SimpleAttributeSet as2
	SimpleAttributeSet as3
	SimpleAttributeSet as4 
	SimpleAttributeSet attr = null
	//CommandSet comset = new CommandSet()	

	def env = [:]
	def stack = []
	int stackMax = 0
	def lookback = -1 // really just stack size

	def title1 = "0        1         2         3         4         5         6         7         8         9         0         1         2         3 2"
	def title2 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012"
	def title3 = "..."
	def title4 = "- - - - - - "

	def t1
	def t2
	def t3
	def t4

	def mono
	def commandPrefix = ""
	def builtincommand = ["go","stop"]
	def tokens			// used in resolveBuiltin
	int at    			// points to zero/none or which builtin command
	String co 			// temp. matcher field

	// class constructor - loads configuration, get system environmental variables; gets hardware window size;
	public Support()
	{
		// Get all system properties
  		props = System.getProperties()
  		OSN = (String)props.get("os.name");
  		pwd = (String)props.get("user.dir");
  		def tokens = OSN.trim().toLowerCase().split(' ').toList()
  		def osid = new StringBuffer();
  		tokens.each{tok -> osid << tok;}
		say("... Support() set to use $osid in pwd=$pwd" )
       	paths = new ConfigSlurper(osid.toString()).parse(new File(pathfile).toURL())
		say("... config is set to use '$osid' paths")
		commandPrefix = paths.commandPrefix		// something like 'open ' on mac os
       	config = new ConfigSlurper().parse(new File(propertyfile).toURL())	// get non-path related static values
       	env = System.getenv()
		getWindowSize()
		mono = new Font("Monospaced", Font.PLAIN, 10)
		say("... Support() ready...$commandPrefix")
	} // end of constructor


	// return a handle to the config file
	public getConfig()
	{
		return config
	}


	// return a handle to the name of the operating system name
	public getOSN()
	{
		return OSN
	}

	// find out what is the command prefix for this platform
	public getCommandPrefix() 
	{
		return commandPrefix
	} // end of 


	// update frame handle
	public setFrame(def fr) 
	{
		frame = fr
	} // end of 


	// find and replace environmental variables
	public findEnv(def txt)
	{
		int ix = txt.indexOf('$');
		if (ix < 0) return txt;
		int ix2 = txt.indexOf(fs); 	// find file separator after $
		if (ix2<0) ix2 = txt.size() 	// didnt find a file sep so use size of text as last ix
		if (ix2 < ix) return txt;
		String substr = txt.substring(ix+1, ix2);
		say substr
		def evar = getEnv(substr)
		substr = txt.substring(ix, ix2);
		say("environmental variable $substr=${evar}") // need logic to xlate $GROOVY_HOME into text plus file.separator
		def replacedText = replace(txt,substr,evar)
		say("${replacedText}")
		return replacedText

	} // end of findEnv


	// find environmental variable value from key
	public getEnv(def ky)
	{
		String va = env[ky]
		if (va==null) return ky
		return va
	} // end of getEnv


    // copied from http://www.exampledepot.com/egs/java.lang/ReplaceString.html?l=rel
    static String replace(String str, String pattern, String replace) 
    {
    	int s = 0;
    	int e = 0;
    	StringBuffer result = new StringBuffer();

    	while ((e = str.indexOf(pattern, s)) >= 0) 
    	{
    		result.append(str.substring(s, e));
    		result.append(replace);
    		s = e+pattern.length();
    	} // end of while

    	result.append(str.substring(s));
    	return result.toString();
    } // end of method


    // F9&UP arrow lookback thru commands
	public getStackEntry(boolean dir)
	{
		def thisStack = ""
		if (stackMax<1) return thisStack	;	// stack.size is zero if no entries in collection

		if (dir)					// scroll up (backwards)
		{	
			if (lookback<1) 
			{ 
				resetStack()
			} // end of if
			else
			{
				thisStack = stack[--lookback]
			} // end of else

		} // end of if
		else
		{
			if (!(lookback<stackMax)) lookback = -1; 
			thisStack = stack[++lookback]
		} // end of else

		return thisStack
	} // end of getStack


	// get current working directory
	public getPWD()
	{
		return pwd 
	} // end of get


	// set current working directory
	public setPWD(def newpwd)
	{
		pwd = newpwd
	} // end of set


	// set lookback pointer to last command in the stack
	public resetStack()
	{
		lookback=stackMax 
	} // end of reset


    // F9&UP arrow lookback thru commands
	public putStack(def entry)
	{
		stack << entry
		stackMax+=1					// stackMax counts how many entries in stack
		resetStack()

	} // end of getStack


	// =========================================================
	// print msg text
	public static void say(String text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	public static void say(String text, boolean f) 
	{
		if (audit) {print "$text";} 
	} // end of say


	// general logic used to position window on this hardware
	public static getWindowSize()
	{
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		wide = dim.width
		high = dim.height
	} // end of getWindowSize


	// method to place window at TOPLEFT,BOTTOMLEFT,TOPRIGHT,BOTTOMRIGHT,or CENTER by default
	public moveWindow(String at)
	{
		Dimension f = frame.getSize();
		left = 0
		top = 0

		//println "moveWindow($at) : w=${f.width} h=${f.height}"
		switch(at)
		{
			case "TL" :
				break

			case "BL" :
				top = high - f.height
				break

			case "TR" :
				left = wide - f.width
				break

			case "BR" :
				left = wide - f.width
				top = high - f.height
				break

			default :
				left = (wide - f.width) / 2
				top = (high - f.height) / 2

		} // end of switch

		if (left<0) left=0;
		if (top<0) top =0;
		frame.setLocation(left,top)
	} // end of MoveWindow


	// look for built-in commands: go <menuname>
	public String resolveBuiltinCommand(String cmd)
	{
		tokens = cmd.tokenize()
		int ts = tokens.size()
		say "cmd '$cmd' has $ts tokens"
		at = 0
		if (ts<1) return cmd;

		// at least one token available (ts>0), so examine it for evidence of builtin command
		// co holds possible builtin command
		co = tokens[0].toLowerCase()			

		int i = 0;

		// match first token against each bic 
		builtincommand.each		
		{ bi -> 
				i+=1; 
				if (bi.equals(co)) 	// found builtin command where at points to that command
				{
					at=i;		// which BIC ?  AT points to it
				} // end of if
		}  // end of each

		say("builtin command is $at with $ts parameters")

		// if it was a builtin command - which one ?
		switch (at)
		{
			case 1: 				// 'GO' builtin command plus one parameter
				if (ts<2 || ts > 2) 
				{
						def tx = "This command requires a single filename parameter"
						showDialog("GO Command Parameters",tx)
						appendText("${tx}", as2);
						cmd=null
				} // end of token count not equal 2: 'go' + <menu name>

				else
				{		// go fred	- load the menu 'fred' with it's options
						// get the menu filename
						def fn = tokens[1].trim()	

						// make sure it ends with .txt
						if (!fn.toLowerCase().endsWith(".txt")) fn += ".txt";		

						def fi = new File(fn)	

						// if menu file does not exists, see if a direct or relative path 
						if (!fi.exists())		
						{				
							// was specified but not found
							// take first char. pointed to a diff path
							def ch = fn.substring(0,1) 

							switch (ch)
							{
								case '/' : break;
								case '.' : break;

								// if no path, add our known ./menudata as prefix
								default :  fn =  config.menufoldername+"/" + fn; break; 
							} // end of switch
						} // end of if


						def f1 = new File(fn)
						cmd = (f1.exists()) ? "¤${fn}" : null 

						say("builtin command resolves to <$cmd>")
						if (cmd==null)
						{
							def tx = "Menu file not found for $fn"
							showDialog("GO Command Parameters",tx)
							appendText("${tx}", as4);
						} // end of if
						else
						{
							//appendText("Return code 0", as2);
						} // end of else
				} // end of else
				break;

			default:
				break;

		} // end of switch

		return cmd
	} // end of resolveBuiltinCommand


	// describe dialog
	public void showDialog(String ti, String tx)
	{
		int messageType = JOptionPane.WARNING_MESSAGE; // no standard icon
		JOptionPane.showMessageDialog(null, "$tx", "${ti}", messageType);
	} // end of showDialog


	// xlate command text with http:// or www. prefix
	public resolveCommand(String co)
	{
		// add some trailing blanks so substring below does not fall over on short commands
		String cmd = co.trim()+"     ";
		int csize = cmd.size()

		// if length of command is short, just return it without further ado.
		if (csize < 2) return co;

		// otherwise, split command into tokens
		def tokens = cmd.split()

		switch ( tokens[0].trim().toLowerCase() )
		{
			// if the apple command to run command to try to open this URL based on it's type
			// known to mac's 
			case "open" :	return cmd;
							break;

			// change working directory so that following commands will have diff. pwd
			case "cd" :		setPWD( tokens[1].trim() )
							break;

			case "pwd" :
			case "command": return cmd;
							break;

			case "sh" :		return cmd;
							break;

			case "echo" :
			case "ls" :		return "command "+cmd.trim()
							break;

			// bash shell is called here
			case "set" :	return "sh -c '${cmd.trim()}' "
							break;

			default : 		break;

		}	// end of switch


		// if it's a groovy or groovyc request then add trailing '&'' to spawn as a sub-task
		//if (cmd.substring(0,6).toLowerCase().equals("groovy")) return cmd +" & ";


		// look for internet naming conventions, 
		// if so call the apple 'open ' prefix to run a browser with this dns name
		if (cmd.substring(0,3).toLowerCase()=="www" ) 
		{
				cmd = getCommandPrefix() + "http://" + cmd
				return cmd
		} // end of if

		// either http or https prefix, then run as apple command
		if (cmd.substring(0,4).toLowerCase()=="http" ) 
		{
				cmd = getCommandPrefix() + cmd
				return cmd
		} // end of if


		// call 'open ' to read any pdf files
		if (cmd.toLowerCase().endsWith("pdf") ) 
		{
				cmd = getCommandPrefix() + cmd
		} // end of if


		// could add more choices here to handle other senarios or file types like images, etc.
		return cmd

	} // end of resolveCommand


	// method to execute immediate command based on number of this menu option; 
	// not used since menu options come from main.txt file now
	//public void runCommand(int option) 
	//{
	//	runCommand(config.menus.commands[option])
	//} // end of runCommand using menu number

	// knock off first 5 char.s
	def fix(x)
	{
		def s = x.substring(5)
	} // end of fix


	// =================================================================================================================
	// method to execute immediate command
	// =================================================================================================================
	public void runCommand(def command) 
	{
		int j = jtp.getText().length() + 3; 
		def proc4

		def sbmjob = (command.trim().endsWith("&")) ? true : false

		// do the business wrapped with try/catch block
		try
		{ 	
			// since * is a shell expansion feature, you cannot pass 'ls menus*' as a command; try sh -c 'ls *.groovy' 
			// Call *execute* on the string
			//WriteOutput(4,"Command=","""${command}""");			
			proc4 = command.execute(null, new File(pwd))                     

			// bailout if command ends with & which forces process to background
			if (sbmjob) 
			{   
				say " - not waiting for process to finish"
				WriteOutput(4,"SBMJOB Ended","");			
				return;
			} // end of if	

			def initialSize = 16384
			def outStream = new ByteArrayOutputStream(initialSize)
			def errStream = new ByteArrayOutputStream(initialSize)
			proc4.consumeProcessOutput(outStream, errStream)


			// extra try-catch pair for process execution
			try {
				proc4.waitForProcessOutput(outStream, errStream)
			}	// end of try

			catch (Exception e)
			{
				say("timeout: ${e.getMessage()}");
				WriteOutput(2,"Wait Timeout","${e.getMessage()}");				
			} // end of catch
			finally
			{
				appendText("Wait For Process to Complete", as0);
				//WriteOutput(9,"Wait For Process to Complete","");				
			}

 
			// Obtain event status and output
			int ev = proc4.exitValue()
			def et = errStream 		// proc4.err.text.trim()
			def so = outStream 		// proc4.in.text.trim()
			attr = as0

			// crude attempt to identify failed task requests, and if so, adjust text color display with text
			// look for pattern evidence of error while running this command; crude and often misses it
			def pattern = ~"^.*(error).*"	
			if (ev==0) attr = as0		// set the text color depending on exit value of command
			if (ev==1) attr = as1
			if (ev==2) attr = as2
			if (ev==3) attr = as3
			if (ev==4) attr = as4


			// audit trail print if audit set
			say("return Code: ${ ev}")	// auditlog reporting
			say("stderr: ${et}")
			say("stdout: ${so}") 		// *out* from the external program is *in* for groovy


			def matcher = et =~ pattern;
			def matcher2 = so =~ pattern;

			// if command execution response was an error, then return the error text
			if (matcher.find() || matcher2.find() ) 
			{
			   attr = as2
			} // end of if 


			//call output
			WriteOutput(ev,et,so);

		}	// end of try

		catch (Exception e)
		{
			say("${ e.getMessage() }")
			jtp.setEditable(true)
			jtp.grabFocus();
			appendText("${e.getMessage()}", as2)

			def issues = problem.displayStackTraceInformation(e,false)
			appendText(issues+":\n", as1);

			int j4 = jtp.getText().length(); 
			jtp.setCaretPosition(j4);
			jtp.setEditable(false)
		} // end of catch

		finally
		{
			appendText("Execute Ended", as4);
		}	// end of finally


		// This did not work as there was no wait for the process to complete, so need a blocking method
		// to hold here til it does; also need solution for commands like man which require several keypresses to cpmplete
		//def cmd = config.menus.commands[option]
		//def result = cmd.execute()
		//println result.text

	} // end of runCommand


	// set up frame title
	public void WriteOutput(ev,et,so)
	{
		// set the text color depending on exit value of command
		switch(ev)
		{
			case 9:
			case 0 : 	attr = as0;	// white
						break;

			case 1 : 	attr = as1	// yellow
						break;

			case 2 : 	attr = as2	// red
						break;

			case 3 : 	attr = as3	// green
						break;

			case 4 : 	attr = as4	// blue
						break;


			default: 	attr = null;
		} // end of switch


		// write output to jtextpane
		// clearText() - could also use setText(null)
		// need editable to be true while updating jtextpane with focus
		jtp.setEditable(true)
		jtp.grabFocus();

		if (ev<9) { appendText("Return Code: ${ev}", as1); }

		// then include any error text or standard output
		if (et.size()>0) appendText("${et}", attr);
		if (so.size()>0) appendText("${so}", attr);

		// figure where to position text cursor, then make it not editable
		int j = jtp.getText().length();
		jtp.setCaretPosition(j);
		jtp.setEditable(false)
	} // end of writeOutput


	// ---------------------------
	// set up frame title
	public String getFrameTitle()
	{
		return getConfig().menutitle.trim() + " " + framefixedtitle
	} // end of label 1


	// set up frame title
	public String getFrameTitle(String t)
	{
		return t.trim() + " " + framefixedtitle
	} // end of label 


	// added this new method to support the new HeaderSupport class which should build a panel of	
	// 3 columns of system menu item names
	// construct a title panel with labels 
	
	public JPanel getHeaders()
	{	
		def p = new JPanel()
		p.setBackground(Color.red);
		p.setOpaque(true)
		p.add(new JLabel("Hi kids"))
		return p
	} // end of method


	// one-time setup of a text pane and styles with colors to be used as the joblog
	public JTextPane getTextPane()
	{
		jtp = new JTextPane();
		jtp.setPreferredSize(new Dimension(780, 410)) 
		jtp.setMaximumSize(new Dimension(780, 510))
        jtp.setFont(mono);
		jtp.setForeground(Color.green);
		jtp.setBackground(Color.black);
		jtp.setEditable(false);

		Border redline = new LineBorder(Color.cyan,1,true);
		jtp.setBorder(null)

		//StyleConstants.setfBold(as0, true);
		//StyleConstants.setItalic(as0, true);

		// zero return code
		as0 = new SimpleAttributeSet();
		StyleConstants.setForeground(as0, Color.white);

		// normal response text
		as1 = new SimpleAttributeSet();
		StyleConstants.setForeground(as1, Color.yellow);
		//StyleConstants.setBold(as1, true);
		
		// echo terminal command bad news in red
		as2 = new SimpleAttributeSet();
		StyleConstants.setForeground(as2, Color.red);

		// echo terminal command
		as3 = new SimpleAttributeSet();
		StyleConstants.setForeground(as3, Color.green);

		// echo terminal command
		as4 = new SimpleAttributeSet();
		StyleConstants.setForeground(as4, Color.cyan);

		return jtp;
	} // end of getText


	// This is logic to populate the joblog panel =========================
    // Clear out current document
	private void clearText() 
	{
		jtp.setStyledDocument (doc = new DefaultStyledDocument());
	} // end of clearText


	// method to add text to pane with specific display attributes
	private void appendText(String s, AttributeSet attributes) 
	{
		String sb = '\n'+s
		doc = jtp.getDocument();
		try 
		{
			doc.insertString(doc.getLength(), sb, attributes);
		}
		catch (BadLocationException e) {}
	} // end of appendText


	private void appendText(String s) 
	{
		doc = jtp.getDocument();
		try 
		{
			doc.insertString(doc.getLength(), s, null);
		}
		catch (BadLocationException e) {}
	} // end of appendText


	// figure out elapsed time of most recent command
	public static computeNano(def nano1)
	{
		def nano2 = System.nanoTime()
		def el = 0.000
		el = (nano2 - nano1) / 1000000000
		//def sf1 = String.format('nanoseconds:%<tN', el)
		def t = "elapsed=${el.toString()} sec.s"
		return t
	} // end of nano compute


    // ============================
	// test harness for this class
	public static void main(String[] args)
	{	
		println "... started"
		Support su = new Support()
		GridBagConstraints c = new GridBagConstraints();
		def ps = new PanelSupport()
		su.jtp = su.getTextPane()
		def cmd = "ls -al"
		su.runCommand(cmd)


		JFrame.setDefaultLookAndFeelDecorated(true);
		def swing = new SwingBuilder()
		def frame = swing.frame(title:"Support Test Harness", background:Color.yellow, pack:false, show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE,size:[900, 700]) 
		{   
			vbox(background:Color.GREEN)
			{
				   panel(layout:new GridBagLayout(), background:Color.BLACK)
				   {
						c.fill = GridBagConstraints.BOTH; // HORIZONTAL
						c.weighty = 0.0;
						c.weightx = 0.0;

						c.gridy = 0; 
						c.gridx = 0;

						// layout headings
						hbox(constraints: c, id:'hd' ) { widget(ps.getPanel()) }
					}	// end of panel

				   panel(layout:new GridBagLayout(), background:Color.BLACK)
				   {
						c.fill = GridBagConstraints.BOTH; // HORIZONTAL
						c.weighty = 0.1;
						c.weightx = 0.0;

						c.gridy = 0; 
						c.gridx = 0;

						hbox(constraints: c, id:'hp' ) { widget(su.jtp) }
					}	// end of panel

							}	// end of vbox

		}	// end of frame

		println "... ended"
	} // end of main


} // end of Support.class

// ================================================================================

/*
== spare logic bits here
 key listener logic here:
    KeyListener keyListener = new KeyListener() {
      public void keyPressed(KeyEvent keyEvent) {
        printIt("Pressed", keyEvent);
      }
      public void keyReleased(KeyEvent keyEvent) {
        printIt("Released", keyEvent);
      }
      public void keyTyped(KeyEvent keyEvent) {
        printIt("Typed", keyEvent);
      }
      private void printIt(String title, KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        String keyText = KeyEvent.getKeyText(keyCode);
        System.out.println(title + " : " + keyText);
      }
    };

	//borderLayout()
			//vbox(constraints: BorderLayout.CENTER)
			vbox()
			{	label "hi kids"
				//menuLines.times{ml -> label(id:'l${ml}',"${ml+1}. ${config.menus.names[ml]}")}
				//vstrut(8)
			} // end of vbox
			panel   //(constraints: BorderLayout.SOUTH)
			{
				tf = textField(id:'choiceTextField', columns: 2, maximumSize: [40, 20],  actionPerformed: { event -> saver() })
				tf.setHorizontalAlignment(JTextField.RIGHT)
			} // end of panel

			'l${ml}'.setHorizontalAlignment(JLabel.LEFT)
			'l${ml}'.setFont(new Font("Courier New", Font.ITALIC, 12));
			'l${ml}'.setForeground(Color.GREEN))
			//def mono = new Font("Courier New", Font.ITALIC, 12)

*/
