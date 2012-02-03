import java.awt.GridBagConstraints.*
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*
import java.awt.*
import groovy.swing.SwingBuilder
import groovy.text.Template
import groovy.text.SimpleTemplateEngine
// need this for property retrieval
import java.io.*;
import java.util.*
import java.awt.BorderLayout as BL
import javax.swing.BorderFactory; 
import javax.swing.border.*
import javax.swing.border.LineBorder
import javax.swing.JFrame
import javax.swing.JTextField
import java.awt.GridLayout
import java.awt.Color
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.event.KeyListener;
import javax.swing.text.*
import java.awt.Graphics
import java.awt.event.WindowEvent;


// class wrapper uses keystroke listener logic
class Menus implements KeyListener 
{

	def static audit = true
	//String propertyfile = './data/menu.properties'  		// non-OS specific parameters for business issues
	def support
	java.util.List<ColumnSupport> cs = []

	def ps = new PanelSupport()

	def static swing
	def static frame	
	def frametitle 
	JTextPane jtp;
	def helpfilename = "./data/help.html"
	def todofilename = "./data/todo.html"
	Border cyanline = new LineBorder(Color.red,1);
	def mono = new Font("Monospaced", Font.PLAIN, 10)

	def bb = new BottomBorder(Color.green)

	GridBagConstraints c = new GridBagConstraints();
	def p1 
	def footer1 = "F1=Help   F2=Allow   F3=Exit   F5=Refresh   F9=Recall   F10/F12=Cancel   F15=All Menus   F17=Cmds"
	def footer2 = " "
	def l1 
	def l2 
	def l3 
	def foc = false
	SimpleAttributeSet as3

	// e.isAltDown()	true if the ALT key was down when this event happened.
	// e.isControlDown()	true if the CTRL key was down when this event happened.
	// e.isShiftDown()	true if the SHIFT key was down when this event happened.
	// e.isMetaDown()	true if the META key was down when this event happened.
	// e.isAltGraphDown()	true if the ALT-GRAPH key was down when this event happened.
	public void keyReleased(KeyEvent ke){}
	public void keyTyped(KeyEvent e)   {}
	public void keyPressed(KeyEvent ke) 
	{
		boolean f = false

		if (ke.isShiftDown()) 
		{
			f = true
		} // end of if

		switch (ke.getKeyCode()) 
		{
			case KeyEvent.VK_F3:  // move x coordinate left
				if (f)
				{
					//println "F15 key pressed"
					String menu = "./data/.menulist.txt"; 
					ColumnSupport.loadMenu(cs,menu)    
					frame.setTitle(ColumnSupport.getFrameTitle())
					support.resetStack()
					swing.tf.text=""
					swing.tf.requestFocusInWindow()
					swing.tf.grabFocus();
				} // end of shift

				else
				{
					//println "F3 key pressed"
					ender()
				} // end of 
				break;

			case KeyEvent.VK_ENTER:  
				if (f)
				{
					//println "Shift+ENTER key pressed"
					swing.tf.text=""
				}
				else
				{
					//println "ENTER key pressed"
				} // end of 
				break;

			case KeyEvent.VK_F2: // allow focus in joblog
				foc = (foc) ? false : true;
				jtp.setFocusable(foc)
				break;


			case KeyEvent.VK_F1: // ask for help
				if (!f)
				{
					helpme(); 
				} // end of shift

				else
				{
					say "F13 key pressed"
					todo();
				} // end of 
				break;


			// F5 --------------------------------------
			case KeyEvent.VK_F5: // reload menu commands

				String menu = ColumnSupport.getStorage().getCurrentMenu(); 

				// use F17 to toggle show/hide of menu items
				if (f) 
				{
					ColumnSupport.loadMenu(cs,menu,true)    
				}	// end of if
				else
				{
					ColumnSupport.loadMenu(cs,menu)    // menuitemsfilename)
				} // end of else

				frame.setTitle(ColumnSupport.getFrameTitle())
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				break;


			// F9 --------------------------------------
			case KeyEvent.VK_F9: // recall prior command
				swing.tf.text = support.getStackEntry(true)
				break;

			// Up Arrow --------------------------------------
			case KeyEvent.VK_UP: // recall prior command - moving backward thru commands from most recent to oldest, then wrap after blank line
				swing.tf.text = support.getStackEntry(true)
				break;


			// Down Arrow --------------------------------------
			case KeyEvent.VK_DOWN: // recall next command
				swing.tf.text = support.getStackEntry(false)
				break;

			// Backstep thru previous menus using either F10 or F12 function keys or the escape key 
			// -------------------------------------			
			case KeyEvent.VK_ESCAPE: 
			case KeyEvent.VK_F12: // mimic F12 for short keyboards
			case KeyEvent.VK_F10: // mimic F12 for short keyboards
				String priormenu = ColumnSupport.getStorage().getPriorMenu()
				String cm = ColumnSupport.getStorage().getCurrentMenu()
				if (!priormenu.equals(cm))
				{
					ColumnSupport.getStorage().pop()
					ColumnSupport.loadMenu(cs,priormenu)    // menuitemsfilename)
					frame.setTitle(ColumnSupport.getFrameTitle())
				} // end of if

				// reset pointer to command stack, then re-focus
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				break;

		} // end of switch

    	} // end of keyPressed



    	// turn on auditlog listing
	public static void setAudit() {audit=true}
	
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say

	// not quite sure which method is used, so declared text as String
	public static void say(String text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// ==========================
	// setup up gui actions
	// ==========================

	// help
	def helpme =  
	{
		def help = helpfilename
		String menu = ColumnSupport.getStorage().getCurrentMenu(); 
		int k = menu.lastIndexOf("/")
		int dot = menu.lastIndexOf(".")
		def menu2 = menu.substring(0,dot) + ".html"
		def name = menu.substring(k+1,k+2).toUpperCase()+menu.substring(k+2,dot).toLowerCase()
                def fi = new File(menu2);
                if (fi.exists()) help = menu2;
		say "helpme menu is <${menu2}> help file will be $help and name is $name"
		File hf = new File(help)
		URL url=null;
		url=hf.toURL();

		HelpWindow hw = new HelpWindow("$name Help Text", url);    
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end of help


	// todo lit
	def todo =  
	{
		def help = todofilename
		say "todo menu is $help"
		File hf = new File(help)
		URL url=null;
		url=hf.toURL();

		HelpWindow hw = new HelpWindow("To Do List", url);    
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end of todo


	// define saver closure
	def saver =
	{ event ->
		def nano1 = System.nanoTime()
		def cmd = swing.tf.text

 		// ignore empty requests
		if (cmd==null || cmd.size()<1 || cmd.trim().equals("") )
		{
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			return;
		} // end of if

		// remove leading/trailing blanks
		cmd = swing.tf.text.trim()		


		int num = 0
		boolean f = true		
		
		def xlate = ""

			if (cmd.size() < 3) 
			{ 	
				try
				{ 
					num = cmd.toInteger()
				} 
				catch(NumberFormatException x) 
				{
					f=false;
				} // end of catch
			} // end of if

			else
			{
				def words = cmd.tokenize()
				try
				{ 
					num = words[0].toInteger()	
				} 
				catch(NumberFormatException x) 
				{
					f=false;
				} // end of catch

			} // end of else
			
			if (f && ( num< 1 || num > ColumnSupport.getMenuOptionCount() ) ) 
			{
				support.appendText("* No such choice: $num", support.as2);
				support.resetStack()
				swing.tf.text=""
				swing.tf.requestFocusInWindow()
				swing.tf.grabFocus();
				return;
			} // end of if

			// translate a menu number choice to the eqivalent text
			if ( num > 0 )
			{
				xlate = ColumnSupport.getMenuCommand(--num)
				cmd = ( cmd.size() > 2 ) ? xlate +  cmd.substring( cmd.indexOf(" ") ) : xlate
			} // end of if

 		// ignore empty requests from internal menu commands
		if (cmd==null || cmd.size()<1 || cmd.trim().equals("") )
		{
			support.appendText("* No command for choice: ${num+1}", support.as2);
			support.resetStack()
			swing.tf.text=""
			swing.tf.requestFocusInWindow()
			swing.tf.grabFocus();
			return;
		} // end of if


		// solve http requests, PDF requests
		cmd = support.resolveCommand(cmd)

		// Echo the String
		support.appendText("\n> ${cmd}", as3)

		// crude attempt to translate environmental variables with $ prefix
		cmd = support.findEnv(cmd)	
		//say("env returned $cmd")

		// remember this command				
		support.putStack(cmd)	

		def tokens = cmd.tokenize()
		def c = tokens[0].trim().toLowerCase()
		switch ( c )
		{
			case "cd" : 
				support.setPWD( tokens[1].trim() ) 
				cmd=null;
				break;

			case "pwd" :
				support.WriteOutput(0, "", support.getPWD() );
				cmd=null;	
				break;

			default :
				cmd = support.resolveBuiltinCommand(cmd)		// convert any builtin commands 
		} // end of else

		if (!(cmd==null))					// a bad way to do this - refactor later
		{
			if (cmd.substring(0,1)=='¤')			// only known existing menu filenames come back here with ¤ prefix
			{
				def fn = cmd.substring(1)
				ColumnSupport.loadMenu( cs, fn)    // menuitemsfilename
				frame.setTitle(ColumnSupport.getFrameTitle())
			} // end of if
			else
			{
				support.runCommand(cmd)
			} // end of else
		} // end of else

		// repaint header
		//support.
		swing.hd.validate()

		// compute timing, clear text field and refocus				
		swing.f2.text = Support.computeNano(nano1)
		swing.tf.text=""
		// does not reclaim focus if URL starts firefox/safari,etc.
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
		return
	} // end of saver


	// setup up gui action to end job
	def ender =
	{ event ->
		System.exit(0)
	}

	// ====================
	// build a swing panel
	public void getPanel(String fn)
	{
		ColumnSupport.loadMenu( cs, fn)

   		say("There are ${ColumnSupport.getMenuItemCount()} menu items")
		support.appendText("O/S=${support.getOSN()} and ${ColumnSupport.getStorage().getCurrentMenu()}");
		support.appendText(" has ${ColumnSupport.getMenuItemCount()} menu choices");

		c = new GridBagConstraints();	
		c.insets = new Insets(0, 1, 0, 1);
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.gridx = 0; // column number where zero is first column
		c.gridy = 0; // row number where zero is first row
		c.anchor = GridBagConstraints.WEST; // direction of drift 4 this component when smaller than window 
		c.fill = GridBagConstraints.NONE; // rule to let a component expand both ways when more space is available on resize

 
		JFrame.setDefaultLookAndFeelDecorated(true);
		swing = new SwingBuilder()
		frame = swing.frame(title:"${frametitle}", background:Color.black, pack:false, show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE,size:[900, 600]) 
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

			c.gridy += 1;
			c.weighty = 0.22;
			c.gridx  = 0

			// layout menu item columns
			hbox(constraints: c)
 		    {			
 		          	sp1 = scrollPane(id:'sp1',border:null,minimumSize:[200,90],preferredSize:[280,200]) {widget(cs[0].getColumn()) }   
			   		label "    "
 		          	sp2 = scrollPane(id:'sp2',border:null,minimumSize:[200,90],preferredSize:[280,200]) {widget(cs[1].getColumn()) }
			   		label "    "
 		          	sp3 = scrollPane(id:'sp3',border:null,minimumSize:[200,90],preferredSize:[280,200]) {widget(cs[2].getColumn()) }
		    }	// end of hbox

		    c.fill = GridBagConstraints.NONE; 	// rule to let a component expand both ways when more space is available on resize
			c.gridwidth = GridBagConstraints.RELATIVE
			c.gridy += 1;  				// row three and first column
			c.gridx = 0;  				// row four and first column
			c.weighty = 0.0;
			c.weightx = 0.0;

			// command entry field
			hbox(constraints: c)
			{
				def t3 = label(id:'t3','Enter menu # or command : ', font:mono, foreground:Color.GREEN)  

				tf = textField(id:'tf', foreground:Color.GREEN, columns: 100, border:bb,  font:mono, actionPerformed: { event -> saver()}, minimumSize:[550, 12], opaque:true, background:Color.BLACK)
				tf.addKeyListener(this);
				tf.setCaretColor(Color.YELLOW)
				tf.getCaret().setBlinkRate(400);
				t3.setHorizontalTextPosition(JLabel.LEFT);
			} // end of hbox


			c.gridy += 1;  				// row four and first column
			c.gridx = 0;  				// first column
			c.weighty = 0.0;
			c.gridwidth = GridBagConstraints.REMAINDER; // makes this component span all columns if last in row

			// function key layout
			hbox(constraints: c)
			{
				label(id:'f1',font:mono,foreground:Color.GREEN, text:"${footer1}",constraints: c)   
				label "    "
				label(id:'f2',font:mono,foreground:Color.YELLOW, text:"${footer2}",constraints: c)   
			} // end of hbox
		   
		    // joblog panel
			c.gridx = 0;  				// first column
			c.weighty = 1.0;
			c.weightx = 1.0;
			c.gridy += 1;  				// row five and first column	
			c.fill = GridBagConstraints.BOTH;
			sp = scrollPane(id:'sp',border:cyanline,constraints: c) {widget(jtp) }

		   } // end of JPanel

		} // end of frame

		
		// store handle to frame, then position frame in center of display
		support.setFrame(frame)
		frame.setTitle(support.getFrameTitle())
		def loc = support.getConfig().location
		// move this frame to position indicated by properties file 
		support.moveWindow(loc) 			

		// gain focus 
		swing.tf.requestFocusInWindow()
		swing.tf.grabFocus();
	} // end getPanel



	// ===============
	// constructor
	// ===============
	public Menus() 
	{ 
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		support = new Support()
   		frametitle = support.getFrameTitle()
/*
   		worker = new SwingWorker()    
   		{  public Object construct() 
   		   {
   		   	   return new expensiveDialogComponent();
   		   } // end of construct
   		};
   		worker.start();
*/

		as3 = new SimpleAttributeSet();
		StyleConstants.setForeground(as3, Color.green);

		3.times{ cs.leftShift ( new ColumnSupport() ) }

   		// build text pane for joblog text
		jtp = support.getTextPane()
		p1 = support.getHeaders() //support.getTitles()

		// false will dis-allow copy/paste from joblog view but tab key not needed
		jtp.setFocusable(false)				

	} // end of default constructor


	// test harness for this class
	public static void main(String[] args)
	{	
		//println "... started"
		setAudit()
		Menus ivs = new Menus()
		ivs.getPanel("./data/main.txt")
		ivs.frame.show()
		//ivs.say("... done ===")

	} // end of main

};    // end of class 


/* ==============================================
* spare logic that works to show info dialog

// also see Main.java code example on background task work
// see http://java.sun.com/products/jfc/tsc/articles/threads/threads1.html  for code fragment
public class Worked extends SwingWorker
{
	SwingWorker worker = new SwingWorker()    
   		{  public Object construct() 
   		   {
   		   	   return new expensiveDialogComponent();
   		   } // end of construct
   		};
} // end of class

def todo = Menus.class.getResourceAsStream(helpfilename).text
int messageType = JOptionPane.INFORMATION_MESSAGE; // no standard icon
JOptionPane.showMessageDialog(null, "$todo", "Menu Help", messageType);
 */