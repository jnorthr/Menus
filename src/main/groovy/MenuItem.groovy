// see: http://groovy.codehaus.org/JN0515-Integers to decode Hex
// use green color sig
import java.awt.Color;
public class MenuItem
{
	private final int menuKey		// sequential numbering key

	int menuBIC		// builtin command number; zero if not builtin;	
	int menuLine		// which line of this column to show this item on
	int menuColumn		// which column to show this item if menuShow=true

	boolean menuBold	// show menu text in bold face
	boolean menuItalics	// show menu text in italics

	private int menuBGColor		// holds background color behind the menu text 
	private int menuTextColor	// holds integer color value of menu text 
	def menuColor		// particular color specification; eventually will hold hex values like 0xc00080 or green; this will be decoded and placed into 

	boolean menuShow	// true to show it

	String menuText		// the text of the menu item before the :=
	def menuCommand		// the full string after the :=

	private int menuIndex		// pointer to array of menu file names that hold this MenuItem

	// the following elements have values if this menu item has a 'go' command to call other menus
	private String dialogTitle	// shows in title of dialog if BIC number says so
	private String menuFileName	// when a BIC of 'go' says load and display another menu, this is the file name to load; typically like ../menudata/menu.txt
    private boolean menuFileExists // true if go menufile confirmed to exist                   

    TextSupport ts;

    // internal temp. var's
    def fi;
    boolean audit = true;
 
	// no args constructor
	public MenuItem()
	{
		menuKey = MenuCounter.getKeyCounter();
		menuBIC = 0;
		menuLine = 0;
		menuColumn = 0;
		menuBold = false;
		menuItalics = false;
		menuBGColor = 0; 	// set to black
	 	menuTextColor = Integer.parseInt("00FF00",16);	// set text to lime
	 	menuColor = "0x00ff00"
		menuShow = false;
		menuText = "";
		menuCommand = "";
		menuIndex = 0;

		dialogTitle = "";
		menuFileName = "";
		menuFileExists = false;

		ts = new TextSupport();
	} // end of constructor


	// ===========================================
	// constructors -
	// simple one arg constructor of text:=command
	public MenuItem(String text)
	{
		this();
		def mt = text.split(":="); println "mt.size()="+mt.size();
		menuText = mt[0].trim();
		menuCommand = (mt.size() > 1 ) ? mt[1].trim() : "";

		// text may have a color setting
		findColorString(menuText)

		// resolve builtin commands
		if (hasGo(menuCommand)) 
        {
            menuBIC = 1;
            def mn = menuCommand.trim()+"   "
            // ignore first 3 char.s ='go '
            menuFileName = findMenuFileName( mn.substring(3) )
            menuFileExists = chkobj(menuFileName);
            def tx4 = "menuFileName $menuFileName does ";
            tx4 += (menuFileExists) ? "" : "not"
            tx4 += " exist;"
            say tx4
        } // end of if

	} // end of constructor


	// simple two arg constructor of text:=command
	public MenuItem(String text,  def command)
	{
		this();
		menuText = text;
		menuCommand = command;

		// text may have a color setting
		findColorString(menuText)

		// resolve builtin commands
		if (hasGo(menuCommand)) 
        {
            menuBIC = 1;
            def mn = menuCommand.trim()+"   "
            // ignore first 3 char.s ='go '
            menuFileName = findMenuFileName( mn.substring(3) )
            menuFileExists = chkobj(menuFileName);
            def tx4 = "menuFileName $menuFileName does ";
            tx4 += (menuFileExists) ? "" : "not"
            tx4 += " exist;"
            say tx4
        } // end of if

	} // end of constructor


	// complex constructor
	public MenuItem(int line, int col, def color, boolean show, String text, def command)
	{
		this();
		menuLine = line;
		menuColumn = col;
		menuBold = false;
		menuItalics = false;
		menuBGColor = 0; 	// set to black
	 	//menuTextColor = setColor(color);	// set text to lime
	 	menuColor = color
		menuShow = show;
		menuText = text;
		menuCommand = command;

		// text may have a color setting
		findColorString(menuText)


		// resolve builtin commands; only the 'go' command will set BIC
		if (hasGo(menuCommand)) 
        {
            menuBIC = 1;
            def mn = menuCommand.trim()+"   "
            // ignore first 3 char.s ='go '
            menuFileName = findMenuFileName( mn.substring(3) )
            menuFileExists = chkobj(menuFileName);
            def tx4 = "menuFileName $menuFileName does ";
            tx4 += (menuFileExists) ? "" : "not "
            tx4 += "exist;"
            say tx4
        } // end of if

	} // end of constructor


	//	---------------------------------------------------------------------------------

	// make some methods to put values into this MenuItem
	void putAt(int n, def c){ menuCommand = c;  }
	void putAt(int n, int c, int l){ if(menuKey == n) {menuColumn = c; menuLine= l;}  }
	void putAt(int n, String o){ if(menuKey == n) menuCommand= o;  }
	def getAt(int n){ if(menuKey == n) return menuCommand }

	// method to return a Color object
	public findColorString(line)
	{
		ColorManager cm = new ColorManager(line);
		menuColor = cm.getHexCode();
        menuText  = cm.getText(); 
		menuTextColor = cm.getColorCode();	
        say "menuColor=<$menuColor> from $line"       
	}	// end of method


	// method to return a Color object
	public getColorObject()
	{
		return new Color( this.menuTextColor );		
	}	// end of method

	// method to return a Color object
	public getColor()
	{
		return this.menuTextColor;		
	}	// end of method

	// temp method to make acolor object from hex color code
	public static applyColor(String hex)
	{
		// Hex to color like 0xc00080
		int intValue = Integer.decode( hex );
		Color aColor = new Color( intValue );
	} // apply color


    // ==================================================================
    // set flag to force audit trail to print
    public void setAudit(boolean f)
    {
        audit = f
    } // end of method

    // ==================================================================
    // method to override compareTo() using spaceship operator <=>
    int compareTo(other) 
    {
        this.menuColumn <=> other.menuColumn && this.menuLine <=> other.menuLine
    } // end of method


   // ==================================================================
   // look for the builtin 'go' command that i used to load another menu
    public hasGo(mc)
    {
        def txt = mc.trim()+"   "
		def x3 = txt.toLowerCase().substring(0,3);
        return (x3.equals("go ")) ? true : false 
    } // end of method


    // ==================================================================
    // look up filename if it exists; print message showing results, if audit flag is set 
    public chkobj(def filename)
    {
        fi = new File(filename);
        def flag2 = (fi.exists()) ? true : false
        say (flag2) ? "file $filename exists" : "file $filename does not exist";
        return flag2
    } // end of method


    // ==================================================================
    // confirm menu exists if the provided file name - possibly massaged - is present
    public findMenuFileName(String ina)
    {
    	def fn = ina.trim()
        // take 1st char. of filename - if . or / then it's absolute and not relative path
        def ch = fn.substring(0,1);
        
        // if menu file name does not end with .txt then add  it
        fn += (fn.toLowerCase().endsWith(".txt")) ? "" : ".txt"  ;
        say "fn = <$fn> and ch=$ch"
        
        // does massaged filename exist ?
        if (chkobj(fn))
        {
            return fn;
        } // end of if file does exist 
        
        if (ch=='.' || ch=='/')
        {
            say "menu file $fn not found !"
            return fn;
        } // end of if
        
        // try alternate look into ./data subfolder for this menu file
        def fn2 = "../menudata/" + fn;
        if (chkobj(fn2))
        {
            return fn2;
         } // end of if file exists 

         say "menu file $fn2 not found either !"
         return fn2;
    } // end of method

    
	// ===============================================================
	// class output debug / print internals
	// print text (maybe)
	public void say(def text) 
	{
		if (audit) println "$text" 
	} // end of say


	// see internal values
	public dump()
	{
		def s = "MenuItem: " +
		"menuKey=${menuKey} " + 
		"menuBIC=${menuBIC} " + 
		"menuLine=${menuLine} " + 
		"menuColumn=${menuColumn} " +

		"menuBold=${menuBold} " + 
		"menuItalics=${menuItalics} " + 
		"menuBGColor=${menuBGColor} " + 
	 	"menuTextColor=${getColor()} " +
	 	"menuColor=${menuColor} " +

	 	"menuShow=${menuShow} " +
	 	"menuText=${menuText} " +
	 	"menuCommand=${menuCommand} " +
	 	"menuIndex=${menuIndex}" +
	 	"dialogTitle=${dialogTitle} " + 
		"menuFileName=${menuFileName} ";
		"menuFileExists=${menuFileExists} ";
		return s;
	} // end of dump


	String toString() 
	{ 
		def md  = (dialogTitle) ? dialogTitle : 'No Menu Title'
		def dt  = md.toString().padLeft(20)

		def mfn = (menuFileName) ? menuFileName : 'No Menufile Name'
		def fn  = mfn.toString().padLeft(20)

		def mex = (menuFileExists) ? "<yes>" : "<no>"
		def mx  = mex.toString().padLeft(5)

		def ms = (menuShow) ? "<yes>" : "<no>"
		def sh  = ms.toString().padLeft(5)

		// location on panels properties
		def mk  = menuKey.toString().padLeft(5)
		def mb  = menuBIC.toString().padLeft(2)	// builtin command
		def ml  = menuLine.toString().padLeft(4)
		def mc  = menuColumn.toString().padLeft(4)

		// style properties
		def bol = (menuBold) ? "<yes>" : "<no>"
		def bo  = bol.toString().padLeft(5)
		def ita = (menuItalics) ? "<yes>" : "<no>"
		def mi  = ita.toString().padLeft(5)

		// color properties
		def bg  = menuBGColor.toString().padRight(8)
		def tc  = menuTextColor.toString().padLeft(1)
		def co  = menuColor.toString().padRight(1)

		def mt  = (menuText) ? menuText.trim() : 'No Text'
		def mtx  = mt.toString().padLeft(17)

		def cmd = (menuCommand) ? menuCommand  : 'No Command'

		def tx  = """\n-----------------------------------------------------\nMI         Title                 Filename       Exists Show   Key BIC  Ln  Col  Bold  Ital  B/G     Color       \nText       Command\n<$menuIndex><$dt> <$fn>$mx $sh $mk $mb $ml $mc $bo $mi   $bg $tc $co\n<$mtx> <$cmd>
		"""	
		//return tx+tx2
	}	// end of method


	// -----------------------------------------------------
	// test harness for this class
	public static void main(String[] args)
	{	
		println "--------------------------------------------------"
		println "... started"
		String c = '0xc00080';
		println "Integer.decode($c)="+Integer.decode(c)
		println MenuItem.applyColor("0xccbbdd");

		//MenuItem mi = new MenuItem()
		//mi.menuKey = 4;  won't work since menuKey is private final
		//assert mi.menuKey == 4

		// menu array
		def ma = []

		println "--- virgin MenuItem"
		MenuItem mi = new MenuItem()
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem("hi:=kids")
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem("hi kids; no colon-equals")
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem(" // notes here")
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem("hi:=")
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem(":=kids")
		ma << mi
		println mi;
		println "---"

		mi = new MenuItem(":=")
		ma << mi
		println mi;
		println "---"




		mi = new MenuItem("hi","kids")
		ma << mi
		println mi;
		println "---"


		println "mi[3] = 99"
		mi[3] = 99
		ma << mi
		println mi
		println "---"
		assert mi[3]==99

		mi = new MenuItem()
		mi[4] = "fred";	// string into the putAt method
		ma << mi
		println mi;
		println "---"
		assert mi[4]=="fred"	// fails on != 
		println "assert mi[4]== fred was ok "
		println "---"

		// int,int,int,int,def,bool,string,def,string,string
		mi = new MenuItem(4,5,'#c00080;',true,"Fred","echo 'fred was here'")
		ma << mi
		println mi
		println "---"

		//assert mi=='echo 'fred was here' '    // can't get the syntax right for this, single,triple or double quotes won't work
		mi = new MenuItem("who woz here:=echo \"jim was here\" ")
		ma << mi
		println mi
		println "---"

		mi = new MenuItem()
		mi.menuBIC = 4;
		mi.menuColumn = 88;
		mi.menuLine = 66;
		mi.menuBold = true;
		mi.menuItalics = true;
		mi.menuBGColor = 255; 
	 	mi.menuColor = "0xffffff"	// this will be overridden if setColor method runs
	 	//mi.menuTextColor = mi.setColor("0xff00ff");	
		mi.menuShow = true;
		mi.menuText = "text";
		mi.menuCommand = "command";
		mi.dialogTitle = "title";
		mi.menuFileName = "filename";
		ma << mi
		println mi
		println "---"

		// ==========================================
		         
        MenuItem menu = new MenuItem("#ccffcc;Display HTML Menu","go this should hold a menu filename");
        println menu;
        ma << menu
                
        MenuItem mi5 = new MenuItem(5,7,"#ccc",true,"Display Jim Panel","go ../menudata/jim.txt");
        println mi5;
        ma << mi5;        

        MenuItem menu2 = new MenuItem("red;Display Max Menu","go ../menudata/max.txt");
        println menu2;
        ma << menu2
        
        MenuItem menu3 = new MenuItem("Display Max2 Menu:= go max2.txt");
        println menu3;
        ma << menu3

        MenuItem menu4 = new MenuItem("Display HTML Menu","go main");
        println menu4;
        ma << menu4


		println "=================================="
		println "\nma size is ${ma.size()} and has these menu items:"
		ma.each{ m -> println m;}
		println "== the end =="
		println "--------------------------------------------------"
	} // end of main

} // end of class
