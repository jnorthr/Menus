// utility class to build text panes of menu items to mimic two/three columns; 
// reads main.txt for any pair of 'text:=command' where := splits both; lines starting with // are comments
import javax.swing.*
import java.awt.*
import javax.swing.text.*;
import java.awt.BorderLayout
import javax.swing.BorderFactory; 
import javax.swing.border.*
import javax.swing.border.LineBorder
 
public class ColumnSupport
{
	// holds decoded menu items filled from static loadMenu method
	def static audit = false		// true will print debug text
	def static menuLines = 0		// how many visible lines appear in the columns
	def static menuOptions = 0		// how many actual menu options appear in the columns which maybe less than menuLines if titles only
	def static menuTitle = []
	def static menuCommand = []	
	def static bicNumber = []	
	def static show = []	
	def static frameTitle="*none*";
	def static notCleared = true
	def static loadCommandText = false

	// keep stack of menu file names - how deep is menu layer that F12 key can use to retrace prior menu choices 
	static Storage storage

	JTextPane column;			// the onscreen representation of the document			
	StyledDocument doc;			// a storage repository for text
	static SimpleAttributeSet as1;		// text decorations to apply to text within 'doc'
	static SimpleAttributeSet asred;	// text decorations to apply to text within 'doc'

	// =============================================
	// class constructor - loads configuration,etc.
	public ColumnSupport()
	{
		// use bold green text	
		as1 = new SimpleAttributeSet()
		StyleConstants.setForeground(as1, Color.green);
		StyleConstants.setBold(as1, true);
		//StyleConstants.setItalic(as1, true);

		asred = new SimpleAttributeSet()
		StyleConstants.setForeground(asred, Color.red);
		StyleConstants.setBold(asred, true);

		doc = new DefaultStyledDocument()
		column = getColumnTextPane(doc);
		clearColumnText(this)
		setColumn(this)
		storage = new Storage()
	} // end of constructor

	// --------------------------------------------
	// Utility methods follow :
	// --------------------------------------------

	// retrieve column text pane and styles with colors to be used as the menu
	public JTextPane getColumn()
	{
		return column;
	} // end of getColumn


	// retrieve pointer to array of menus
	public static getStorage()
	{
		return storage;
	} // end of getCommands


	// retrieve array of commands
	public static getCommands()
	{
		return menuCommand;
	} // end of getCommands


	// retrieve menu title for frame
	public static getFrameTitle()
	{
		return frameTitle;
	} // end of getFrameTitle


	// store menu title for frame
	public static setFrameTitle(def nt)
	{
		frameTitle = nt;
	} // end of setFrameTitle


    // turn on auditlog listing
	public static void setAudit() {audit=true}

	
	// print debug text (maybe)
	public static void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of say


	// find text equivalent of this menu option number
	public static String getMenuCommand(int opt)
	{
		return menuCommand[opt]
	} // end of getMenuCommand


	// the maximum number of visible option lines, which might be divided into several columns
	public static int getMenuItemCount()
	{
		return menuLines 
	} // end of method
	

	// the highest option number assigned - the max value to key on command line before it's an error
	public static int getMenuOptionCount()
	{
		return menuOptions
	} // end of method


	// ---------------------------------------------
	// method to clean and reset variables
	public static void cleanup(def nc, menufilename)
	{
		// only clear variables if this menu file has at least one := command
		if (nc)
		{  
			say " - cleanup"     
				notCleared=false
				menuLines = 0
				menuOptions = 0
				bicNumber = []
				menuTitle = []  	// this text is what appears on the menu panel
				menuCommand = []	// this is the command to be executed if this option is chosen
				show = []
				setFrameTitle("$menufilename")
		} // end of if
	}	// end of cleanup


	// --------------------------------------------------------
	// load a menu without showing the 'command' as menu text, 
	// just show text for menu entry as normal
    public static void loadMenu(def cs, def mifilename)
    {  
		loadMenu(cs, mifilename,false)
	} // end of method


	// ---------------------------------------------------------------------
    // Originally had menu options and commands as part of properties file 
    // but now these are found in their own .txt file.
    // This method reloads them and divides 1st third into left column, 
    // the remainder into the middle and right columns.
    //
	// cs is array of menu columns, 

	// mifilename is the name of the menu .txt file 

	// showCommandText is a boolean to show the actual command rather 
	// than the menu text ( function key F17 used as toggle )
	//
	public static void loadMenu(def cs, def mifilename, def showCommandText)
	{  
		notCleared = true	// flag to avoid clearing prior menu if current menu file does not have at least 1 menu item;

		def f = new File(mifilename)   // get handle for the menu text file
		def words
		int ix2 = 0
		say "opening menu file $mifilename"

		// how many menu items ?
		f.eachLine 		// walk thru each line of menu file ignoring comment lines starting with //
		{	aline ->

			say " - $aline"
			// code to ignore comment lines
			def useme = true		// true when this line is a possibility to hold :=
			ix2 = aline.trim().indexOf("//")

			useme = true 		// ignore comment lines starting with //
			switch (ix2) 
			{
				case 0..3 : 	useme = false;
								break;
			}  // end of switch

			// if not a comment and line has := then it can be used as a menu entry, so split it in two parts.
			if (useme && aline =~ /^.*\:=.*/) 		// The signature for a menu option is text:=command
			{
				// static method to reset internal variables
				ColumnSupport.cleanup(notCleared, mifilename)

				// break menu entry into 2 parts: 1) option text description 2) option command
				words = aline.split(/\:=/)	
				int wc = words.size()		// how many tokens ? Normally two if := 

				def word1 = ""
				def word2 = ""
				// set true when the command pair form a valid command
				boolean flag = false	

				// set to zero unless this is an internal menu feature, a built-in command
				int bic = 0		

				// word count governs how it's handled
				switch (wc)		
				{
					// typical text:=command
					case 2:		
						flag = true
						word1=words[0].trim()
						word2=words[1].trim()
						if ( word1.size()<1 )
						{
							flag = false
						} // end of if

						// was there any command text ?
						if ( word2.size() >0 )
						{	
							// figure out built-in commands
							say " - <"+word2+">"
							if (word2.toLowerCase().equals("*menutitle")) {bic = 1;}
							if (word2.toLowerCase().equals("*red")) {bic = 2;}
							say " - bic set to $bic";
						} // end of if
						break;

					// a word count of one means line format was 'xxx:='  without text after :=
					// was this for menu text only displays ?
					case 1:
						flag = true
						bic = 3
						word1=words[0].trim(); //say "word0=<$word1>"
						break;

					default:
						say "unknown wc=$wc for line <${aline}>"
						bic = 99
						break;
				} // end of switch

				// -------------------------------
				// this is a valid pair, so store
				if (flag)
				{	 
					say " - word1=<${word1}> bic=$bic and flag=$flag";

					// identify the builtin command or zero if normal menu option
					switch(bic)	
					{
					    case 99:	break;

					    // menu text only - not a command so allocate no number to it
					    case 3:
	    					menuLines += 1
					    	bicNumber << bic
    						menuTitle   << word1
					    	break;

					    // color sample - *RED
					    case 2:
    						menuLines += 1
					    	bicNumber << bic
    						menuTitle   << word1
					    	break;

					    // *MENUTITLE
					    case 1: 
					   	   	setFrameTitle(word1) // this is a menu title not a command sequence
				   		   	break;

				   	   // typical built-in command of zero
					    default:
					    	bicNumber << bic
    						menuLines += 1
    						menuCommand << word2

							if (showCommandText)
							{
								def tx =  (word2.size() > 40) ? word2.substring(0,40)   : word2
								word1 = tx
							} // end of if

							menuTitle << word1; // (showCommandText) ? word2 : word1;	

	    					break;
					} // end of switch

					say " - there are $menuLines menu lines"
				} // end of if

			} // end of aLine is a menu command
			
		} // end of eachLine

		// -------------------------------------------------------------------------------------
		// do not disturb current menu if this menu file has no lines with := command identifier
		if (notCleared) return		


		// if the new menu name is not the same as the current menu name, then stack it
		if (!(storage.getCurrentMenu().equals(mifilename)))
		{
			storage.leftShift(mifilename)
		} // end of if

    	// erase each menu item column
		cs.eachWithIndex{ va, ix -> cs[ix].clearColumnText(cs[ix]) }

    	// =====================================================================================================
   		// walk thru each title and place 1/3 of menu items in each column 1 and 2 with remainder in column 3
   		int i = 0;
   		int k = 0;
   		def num
   		int percolumn = 0 
   		int[] itemcount = [0,0,0]
   		boolean[] itemflag = [true,true,true]
   		int ic = itemcount.size()


   		// with less than 8 menu items, just use a single column
   		if (menuLines < 8)
   		{
   			percolumn = menuLines
   			itemcount[0] = menuLines
   		}
   		else
   		{
   			// there are enough menu items to divide between 3 columns
   			percolumn = menuLines / 3
   			ic.times{item -> itemcount[item] = percolumn}
   			if (percolumn * 3 != menuLines) 	// if not modulo three
   			{
   				percolumn+=1;
   				say "even number of items per col: $percolumn";
   			} // end of if
   		} // end of else


   		say("there are ${menuLines} menu items of $percolumn items per column")
   		int thiscolumn = 0
   		say "there are ${menuTitle.size()} menuTitles"

   		// now roll thru the titles and assign them to each of 3 columns
   		menuTitle.eachWithIndex
   		{	it, ix ->

   			say "menuTitle.each=$it and ${menuCommand[ix]}"
   			say "menu $it and ix=$ix and bic=${bicNumber[ix-1]}"
	
			thiscolumn = (ix<1) ? 0  : ix / percolumn;	// compute a column number as either zero, one, two

   			// give every menu item a leading c/r
   			num = "\n"				

			// if this is the first time thru for this column, erase the leading c/r
			if (itemflag[thiscolumn]==true) 	
			{
				num="";
			} // end of if


			// (!(menuCommand[i].equals("")))
			if (bicNumber[ix]<1)						
			{
				k+=1
				if (k<10) num +=" "			// if one digit menu option number then add a leading blank
				num += "${k}. ${it}"		// logic to add a nice blank to number plus . when formatting
				menuOptions = k				// remember the maximum option number allowed to be keyed from command line
			} // end of if

			// different formatting for built-in commands above '1' -  
			if (bicNumber[ix]>1)	
			{
				num += "    ${it}"			
			} // end of else


			// normal non-BIC one formatting
			if (!(bicNumber[ix-1]==1))
			{
			   cs[thiscolumn].appendColumnText(cs[thiscolumn], num, as1)			
			   itemflag[thiscolumn] = false			// no longer first time thru
			} // end of if

   		} // end of walking thru each title 


   		// find how many blank lines go in final column
   		int j = ( ( thiscolumn + 1 ) * percolumn ) - menuTitle.size()		
   		say("i=$i and j=$j and thiscolumn=$thiscolumn and percolumn=$percolumn")

   		// add an extra blank m/i entry to make column 3 look nicer on odd m/i count
   		if (j > -1) { j.times{ cs[2].appendColumnText(cs[2], "\n") } } 	

    } // end of load


    // ============================================
	// declare text attributes and mono font usage
	public void setColumn(ColumnSupport su)
	{
		def column1 = su.getColumn()
		//column1.setBackground(Color.black);
		column1.setFocusable(false)
		column1.setEditable(false);
		//column1.setFont(new Font("Dialog", Font.PLAIN, 10));
		column1.setMinimumSize(new Dimension(80,60));
		column1.setPreferredSize(new Dimension(380,160));
		column1.setMaximumSize(new Dimension(400,280));
	} // end of setting attributes 


	// one-time setup of a column text pane and styles with colors to be used as the menu
	public JTextPane getColumnTextPane(StyledDocument document)
	{
		JTextPane jtp = new JTextPane(document);
		jtp.setBorder(null)
		jtp.setMinimumSize(new Dimension(80,60));
		Font font = new Font("Dialog", Font.PLAIN, 10);
        jtp.setFont(font);
        jtp.setForeground(Color.green);             
        jtp.setBackground(Color.black);             

		return jtp;
	} // end of getText


	// -------------------------------------------------------
	// This is logic to populate the joblog of the menu panel 
	// ==========================
    // Clear out current document
	private void clearColumnText(ColumnSupport su) 
	{
		su.getColumn().setStyledDocument (doc = new DefaultStyledDocument());
	} // end of clearColumnText


	// method to add text to column pane with default display attributes
	private void appendColumnText(ColumnSupport su, String s) 
	{
		doc = su.getColumn().getDocument();
		try 
		{
			doc.insertString(doc.getLength(), s, as1);
		}
		catch (BadLocationException e) {}
	} // end of appendColumnText


	// method to add text to column pane with specific display attributes
	private void appendColumnText(ColumnSupport su, String s, SimpleAttributeSet sas) 
	{
		doc = su.getColumn().getDocument();
		try 
		{
			doc.insertString(doc.getLength(), s, sas);
		}
		catch (BadLocationException e) {}
	} // end of appendColumnText


	// ==================================================
	// test harness for this class
	public static void main(String[] args)
	{	
		say "... started"
		java.util.List<ColumnSupport> cs = []

		ColumnSupport su1 = new ColumnSupport()
		su1.clearColumnText(su1)
		cs << su1;

		JFrame frame = new JFrame("TextPane Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout( new FlowLayout() )
		frame.setBackground(Color.black)
		frame.add(su1.getColumn())
		frame.setSize(1000, 260);
		frame.setMinimumSize(new Dimension(1000,260));

		cs << new ColumnSupport()
		cs << new ColumnSupport()
		ColumnSupport.loadMenu(cs, "../menudata/stylesheets.txt", true)      

		// report commands found
		say "... Commands are :"
		def com = []
		com = ColumnSupport.getCommands()
		com.each{cmd -> say cmd}
		//scrollPane = new JScrollPane(su2.getColumn());
		frame.add(cs[0].getColumn());
		frame.add(cs[1].getColumn())
		frame.add(cs[2].getColumn())
		frame.pack()
		frame.setVisible(true);

		say "... ended"
	} // end of main

} // end of ColumnSupport.class



// =====================================================================================
/* spare logic from runcommand()
				//swing.p1 = support.getTitles()
			//sp.getHorizontalScrollbar().setValue(int Pos);
			//sp.getVerticalScrollbar().setValue(int Pos); 
			//JScrollBar sb = swing.sp.getHorizontalScrollbar()
			//int sph = sb.getValue()
			//int spv = swing.sp.getVerticalScrollbar().getValue()

*/
/*  -----------------------------------------
   this sample tried to create a menu within a panel but spacing was an issue
			panel(constraints: c)  // ,opaque:true,background:Color.black)
			{
			   vbox()
			   {
				menuLines.times
				{ml -> 
					def no
					if (ml<9) 
						{no = " ${ml+1}"}
					else
						{no = "${ml+1}"}
					def mn = "${menuTitle[ml]}"
					label(id:'l${ml}',font:mono, foreground:Color.GREEN, text:"${no}. ${mn}")  // ,preferredSize:[350, 12])
				} // end of ml
			   } // end of vbox
			} // end of panel


	//Border greenline = BorderFactory.createLineBorder(Color.green,1,true);
	//Border greenline = new LineBorder(Color.green,1,true);

*/


