// what about missing lines between items
// count false flags at end of each col.
// see if any false b4 last true
// then balance out items/column
// or disallow constructor to enter col/row
public class MenuArray
{
    boolean audit = true
    
    // linear menu array list of MenuItem's 
    def ma
    def columns    
    def cells    // 33 places to hold a boolean; 3 columns X 11 rows = 33 places; set TRUE when place has been used
    def data
    final Integer MAXMENULINES = 13;
    final Integer MAXMENUCOLUMNS = 3;
    final Integer MAXMENUENTRIES = MAXMENUCOLUMNS * MAXMENULINES;


    // ================================================
    // constructor
    public MenuArray()
    {
        ma = []
        columns = 0;
        cells=new Object[MAXMENUCOLUMNS][MAXMENULINES]
        (0..<cells.size()).each{ c ->  
            (0..<cells[c].size()).each{r->              
                 cells[c][r]=-1;} 
        }        
    } // end of constructor
    
    // ================================================
    // constructor to load menu & do all necessary bits to make this array ready to use
    public MenuArray(def menuname)
    {
        this();
        loadMenu( menuname );
        assignKeys();
 	int count = countUsedCells();
        setUsedCells();
        sortCells();
    } // end of constructor
    

    // ================================================
    String toString() 
    {
        def txt = "\nShow   BIC   Key  Column Row  Menu File Name                  Color      Exists Text                                     Command\n"
           txt += "----   ---   ---  ------ ---  -----------------------------   --------   ------ ----------------------------------       -------------------------------\n"
        ma.each{ m -> 
		def txt1 = (m.menuShow) ? "Yes" : "no "; 
	        txt += "$txt1   ${m.menuBIC.toString().padLeft(3)} ${m.menuKey.toString().padLeft(5)} ${m.menuColumn.toString().padLeft(5)} ${m.menuLine.toString().padLeft(5)}   ${m.menuFileName.padRight(30)}  ${m.menuColor.padRight(2)}   ${ (m.menuFileExists) ? "Yes" : "no "; }    ${m.menuText.padRight(40)} ${m.menuCommand}\n";
        } // end of each
        txt += "----------------------------------------------------------------------------------------------"
        return txt
    } // end of method

    
    // ================================================
    // method to check if a column/row cell has not been used
    public boolean isCell(c,r) { return (cells[c][r]<0) ? true : false; } 
    
    
    // ================================================
    // method to remember that a column/row cell has not been used
    public boolean setCells(c,r,ix) { if (isCell(c,r)) {cells[c][r]=ix; return true; } else { return false; } }
    
    // ================================================
    // return the full list of menu items as an array[]
    public getMenuArray() { return ma }

    // ================================================
    // how many actual menu entries ? showable or not ?
    public getMenuCount() { return ma.size() }
    
    // ================================================
    // how many actual menu entries that are showable ?
    public getShowCount()
    {
        int cnt = 0
  	getMenuArray().each{ if ( it.menuShow ) cnt+=1 }
        return cnt;
    } // end of method
          

    // ================================================
    // sort twice: first time on most minor key and again on most major key
    public sortCells()
    {
        ma.sort( { k1, k2 -> k1.menuLine <=> k2.menuLine } as Comparator )*.menuLine
        ma.sort( { k1, k2 -> k1.menuColumn <=> k2.menuColumn } as Comparator )*.menuColumn    
    } // end of sort


    // ================================================
    // (re)assign entry keys
    public assignKeys()
    {
        getMenuArray().eachWithIndex{ m, ix ->  ma[ix].menuKey = ix; } // end of each    
    } // end of method

    // ========================================================================================================
    // how many cells 2b shown ?
    public int countUsedCells()
    {
        say "Method countUsedCells() ------------------"
	int counter = 0;
        (0..<getMenuCount()).each { ix -> 
            counter +=  (ma[ix].menuShow) ? 1 : 0 ; 
	} // end of each

        say " - counted $counter items."
	return counter;
    } // end of method

    // ========================================================================================================
    // really need to do this twice: once for how many and then compute per column = how many / MAXPERCOLUMN
    // set flag if column/row already used
    public setUsedCells()
    {
        say "\nMethod setUsedCells() ------------------\nRe-assign columns and rows for ${getMenuCount()} items"
        int co = 1;
        int li = 1;
        int totalentries = 0;
        
        int maxpercol = getShowCount() / MAXMENUCOLUMNS;
	if ( getShowCount() / MAXMENUCOLUMNS != maxpercol) maxpercol += 1;
        say "  allow $maxpercol max rows per column for each of $MAXMENUCOLUMNS columns" 
        
        (0..<getMenuCount()).each { ix -> 
            tell "\ndoing $ix : ";
            if (ma[ix].menuShow) 
            {   
                tell "has menuShow=true"
                tell "$ix starts with column ${ma[ix].menuColumn} at line ${ma[ix].menuLine}"
                
                int c = ma[ix].menuColumn
                int r = ma[ix].menuLine
                def used = false;
                while (!(used))
                {
                    used = setCells(c, r, ix);
                    
                    // if used then count this entry
                    if (used) 
                    {
                        totalentries += 1;
                        ma[ix].menuColumn = c+1;
                        ma[ix].menuLine = r+1;
                    } 

                    // avoid table overflow
                    if ( !(totalentries < MAXMENUCOLUMNS * maxpercol ) ) 
                    {
                        used = true;
                        ma[ix].menuShow = false;
                        say "too many menu items to display; ignored $ix entry"
                    } // end if total...

                    
                    if (!used)
                    {
                        if ( r < MAXMENULINES )
                        {
                            r += 1;
say "r < $MAXMENULINES - 1 so bumped r=$r"
                        }
                        else
                        {
                            // too many in prior column, start new column pf entries
                            if ( c  <  maxpercol )
                            {
                                c += 1;
                                r = 0;
                            }
                            else
                            {
                                say "too many menu items to display; ignored $ix entry"
                                used = true;
                                ma[ix].menuShow = false;                                
                            } // end of else    
                        } // end of if max lines
                        
                        say " - set to column ${ma[ix].menuColumn} line ${ma[ix].menuLine}"
                    } // end of if
                } // end of while not used
                
                say " - ends with column ${ma[ix].menuColumn} at line ${ma[ix].menuLine}"
            
            }
            else
            {
                say "has menuShow=false"
            } // end of else    
        } // end of each

        cells.eachWithIndex{ix,v -> say "- cell $ix is $v"}
    } // end of method

    // ================================================
    def static say(txt)
    {
        println txt
    }

    // ================================================
    // print debug text (maybe)
    public void say(def text) 
    {
        if (audit) println "MA : ${text.toString()}" 
    } // end of say
 
    // ================================================
    // print debug text (maybe)
    def tell(def text) 
    {
        if (audit) print "<$text>" 
    } // end of tell

    // ================================================
    // print debug text (maybe)
    static prt(def text) 
    {
        println "MA : $text" 
    } // end of prtprt
    //=======================================
 
    // ================================================
    // originally had menu load as part of ColumnSupport
    public void loadMenu(menuname)
    {  
	def menudata
            try 
            {
                def men = new File(menuname)
                say "opening $menuname menu as ${men.canonicalPath} at ${men.lastModified()}"
                menudata = men.getText();
            } 

            catch (FileNotFoundException e) 
            {
                System.err.println("FileNotFoundException: "+ e.getMessage());
                throw new Exception(e);
            }        

            boolean notCleared = true

            def words
            def ix2=0
            def menuLines = 0
            def menuOptions = 0
            MenuItem mi = new MenuItem();

            
            // how many menu items ?
            // signature for a menu option is text:=command
            menudata.eachLine         // walk thru each line of menu file ignoring comment lines starting with //
            {   aline ->
                say aline;
                // if not a comment and line has := then split
                if (!(aline.trim().startsWith("//")) && aline =~ /^.*\:=.*/)         
                {    
                    ++ix2                
                    
                    // only clear existing menu variables if this menu file has at least one := command
                    if (notCleared)
                    {       
                        notCleared=false
                        menuLines = 0
                        menuOptions = 0
                        //bicNumber = []
                        //menuTitle = []      // this text is what appears on the menu panel
                        //menuCommand = []    // this is the command to be executed if this option is chosen
                        //setFrameTitle("$mifilename")
                    } // end of if

                    words = aline.split(/\:=/)        // break menu option into 2 parts: 1) option text description 2) option command
                    int wc = words.size()
                    def word1 = ""
                    def word2 = ""
                    boolean flag = false    // set true when the command pair form a valid command
                    int bic = 0        // set to zero unless this is an internal menu feature, a built-in command
                    switch (wc)        // word count governs how it's handled
                    {
                        case 2:
                            word1=words[0].trim()
                            word2=words[1].trim()
                            flag = ( word1.size() < 1 ) ? false : true
                            break;

                        // a word count of one means line format was 'xxx:='  without text after :=
                        // was this for menu text only displays ?
                        case 1:
                            flag = true
                            bic = 10
                            word1=words[0].trim(); 
                            say "word0=<$word1>"
                            break;
                    default:
                        say "unknown wc=$wc for line <${aline}>"
                        bic = 99
                        break;
                    } // end of switch


                    // this is a valid pair, so store
                    if (flag)
                    {    
                        say """  word1=<${word1}> bic=$bic""";
                        //bicNumber << bic

                        switch(bic)    // identify the builtin command or zero if normal menu option
                        {
                            case 99:    break;


                            // *MENUTITLE
                            case 90: 
                            break;

                            // typical bid command of zero
                            default:
                            menuLines += 1
                            mi = new MenuItem(menuLines,word1,word2);
			    say "---> this is menu line $menuLines : "
			    say mi;
                            this.ma << mi;                            
                            break;
                        } // end of switch

                     } // end of if

                } // end of if
                
             } // end of eachLine

             say ". . . found $ix2 lines and menuLines=$menuLines"
    } // end of method
 
 


    // =======================================================================================
    // test harness for this class
    // =======================================================================================
    public static void main(String[] args)
    {    
        prt "... started"

        MenuArray me = new MenuArray();
        
        // must use basic constructor with seq.key and command text; only initializes the cells[]
        MenuItem mi = new MenuItem();
        prt mi;
        
        mi[1] = "echo 'hi kids'"; 
        prt mi;
        assert !mi[1].equals("jim")
        me.ma << mi
        
        // this does not really work because the current mi has a key of 1 not 2, as you can see when you print the mi
        mi[2] = "fred"
        prt "changed mi[2] to fred but = "+mi;
        assert !(mi[2]=="fred")    // fails on == 
        mi = new MenuItem(55,"Display Jim Panel 1","go ./data/jim.txt");
        //mi = new MenuItem(2,2,'C','Y',"Fred","echo 'fred was here'",1)
        prt mi
        //assert mi=='echo 'fred was here' '    // can't get the syntax right for this, single,triple or double quotes won't work
        me.ma << mi
        
        // must use basic constructor with seq.key and command text
        mi = new MenuItem();
        prt "basically, an empty mi = "+mi;
        mi = new MenuItem(8,"groovy -v");
        prt "an mi with no menu call = "+mi;
        me.ma << mi;
         
	// this flavor of constructor initializes a menu item and loads menu title and menu item text & command from  provided parm values
        MenuItem menu = new MenuItem(4,"Display HTML Menu","go ./data/html.txt");
        prt menu;
	// optionally stuff this menu item into a menu array[]
        me.ma << menu
        menu.menuFileName="this should hold a menu filename"
        //menu.confirmMenu(menu.menuFileName)
                
	// this flavor of constructor is mre complex & initializes a menu item, set color of text and column and row placement, several flags and then loads menu title, 
	// menu item text & command from  provided parm values
        MenuItem mi5 = new MenuItem(15,7,"green",true,"Display Jim Panel 2","go ./data/jim.txt","Main Menu Title","./data/jim.txt", true);
        prt mi5;
        me.ma << mi5;        

	// simple constructor using a key of 22 then stuff it into the menu array
        MenuItem menu2 = new MenuItem(22,"Display 22","nano ./data/max.txt");
        prt menu2;
        me.ma << menu2
        
        MenuItem menu3 = new MenuItem(23,"Display 23","groovyc -v");
        prt menu3;
        me.ma << menu3


        MenuItem menu4 = new MenuItem(24,"Display 24","groovy -v");
        prt menu4;
        me.ma << menu4

        MenuItem menu5 = new MenuItem(25,"Display 25","env");
        prt menu5;
        me.ma << menu5

        // here the array keys are re-ordered to allow better random access by key value        
        prt "===================================================================================="
        prt "\nre-assign keys"
        me.assignKeys();
        
                
        // here each entry of the array has it's column and row locations re-ordered for presentation
        prt "===================================================================================="
        // re-assign columns and rows
	int c1 = me.countUsedCells() 
        prt "MA : me.counted $c1 items"
        me.setUsedCells();

        // here the array is reordered by column and row locations                 
        prt "===================================================================================="
        prt "\nMA : checking sort()"
        prt "\nMA : re-assign columns and rows for ${me.getMenuCount()} items"
        me.sortCells();
    
	// how many menu items are there ?	
        me.columns = me.getShowCount() / 3;
        prt "\nMA : ma size is ${me.getMenuCount()} of which ${me.getShowCount()} will appear with these menu items:"
        prt me
                
        prt "===================================================================================="
                
	// initialize brand new array with menu title and menu items loaded from a text file, then re-keyed and re-ordered
        // read from main.txt menu file        
        MenuArray array = new MenuArray("./data/main.txt");

        prt "\nMA : re-assign keys"
        array.assignKeys();
        prt array;         
        
        // re-assign columns and rows 
	int c2 = array.countUsedCells() 
        prt "MA : array.counted $c2 items"
        array.setUsedCells();

        prt "\nMA : checking sort()"
        prt "re-assign columns and rows for ${me.getMenuCount()} items"
        array.sortCells();
    
        array.columns = array.getShowCount() / 3;
        prt "Max rows/column is $array.MAXMENULINES; Max Columns is $array.MAXMENUCOLUMNS allowing $array.MAXMENUENTRIES entries in this menu"
        prt "ma size is ${array.getMenuCount()} of which ${array.getShowCount()} will appear with these menu items:"        
        prt array;         
                
        prt "\nMA : create mae array then load another menufile"
        MenuArray mae = new MenuArray();
	mae.loadMenu("./data/html.txt");
        mae.assignKeys();
	int c3 = mae.countUsedCells() 
        prt "MA : mae.counted $c3 items"
        mae.setUsedCells();
        mae.sortCells();

	// dump content of latest menu array
        prt "mae size is ${mae.getMenuCount()} of which ${mae.getShowCount()} will appear with these menu items:"        
        prt mae;         
        prt "== the end =="
    } // end of main

} // end of class