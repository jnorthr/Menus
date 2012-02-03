// utility class to build text panes of menu items to mimic multiple columns; 
// see HeaderSupportTest.groovy for test harness 
import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*
import javax.swing.text.*;

public class HeaderSupport
{
    JTextPane column;            	// the onscreen representation of the document            
    StyledDocument doc;            	// a storage repository for text
    static SimpleAttributeSet as1;  // text decorations to apply to text within 'doc'
    static audit = false			// flag to turn audit messages to console
    static CommandSet cs;

    // class constructor - loads configuration,etc.
    public HeaderSupport()
    {
        // use white text    
        as1 = new SimpleAttributeSet()
        StyleConstants.setForeground(as1, Color.white);
        //StyleConstants.setBold(as1, true);
        //StyleConstants.setItalic(as1, true);
        doc = new DefaultStyledDocument()
        column = getColumnTextPane(doc);
        clearColumnText(this)
        setColumn(this)

        cs = new CommandSet();
        cs.setDate();
    } // end of constructor


    // retrieve column text pane and styles with colors to be used as the menu
    public JTextPane getColumn()
    {
        return column;
    } // end of getColumn
    

    // print debug text (maybe)
    public static void say(def text) 
    {
        if (audit) {println "$text";} 
    } // end of say


    // originally had menu options and commands as part of properties file but now these are found in their own .txt file
    // this method reloads them and divides 1st third into left column, the remainder into the middle and right columns
    // mifilename is name of menu file to load
    public static void loadMenu(columns,menuitems) 
    {  
    	say "loadMenu..."
       	int numberofcolumns = columns.size()	// how many columns this time ?
        def itemflag = [numberofcolumns]
        def itemcount = [numberofcolumns]


        // =====================================================================================================
       	// walk thru each title and place first third of menu items in column 1 and 2 with remainder in column 3
        //int i = 0;

        // there are enough menu items to divide between columns
        int percolumn = menuitems.size() / numberofcolumns

        if (percolumn * numberofcolumns != menuitems.size())     // if not modulo three
        {
        	percolumn+=1;
            say "not even number of items per col: $percolumn";
        } // end of if

        // erase each menu item column, set flags true and items/per/column count ot zero
    	numberofcolumns.times
    	{ ix -> 
    		itemflag[ix] = true;
    		itemcount[ix] = percolumn;
    	} // end of each

        columns.each { it.clearColumnText(it); }

        say("there are ${menuitems.size()} menu items of $percolumn items per column in $numberofcolumns columns")
    	say("   the max entries for this table is ${percolumn * numberofcolumns}")

        int thiscolumn = 0
        def val
    	String tx
    	String whole
    	int x = 0

        // now roll thru the titles and assign them to each of the columns
        menuitems.eachWithIndex
        {   
    		it, ix ->   

        		say "\nmenuitem <$it> and ix=$ix "
                thiscolumn = ix / percolumn;    // compute a column number for this menu item to appear in

        		tx = it.key.trim() 

        		// see if more than one environment var. name is present, i.e. comma is in map value.
        		// like "User :":"user,uid",  from menu.properties; so map key User : points to map value "user,uid"
        		// requiring two values from CommandSet map loaded at boj. Resulting in User : jim 502

        		// only need a single text value as map value is a single key to look up an environmental value
               	if ( it.value.indexOf(",") < 1) 
        		{ 
        			val = " "+cs.getMap(it.value.trim()) 
        		}

        		// else there are two (but not more than are coded for) text values to place in header column of menu
        		else  
        		{
        			def tokens = it.value.trim().split(",")
        			val = " "+cs.getMap(tokens[0].trim()) 
        			val += " "+cs.getMap(tokens[1].trim()) 
        		} // end of else


        		int pad = 0
        		if ( thiscolumn.equals(1) )
        		{ 
        			if ( val.size() + x < 40) 
        			{ 
        				int p = 40 - ( val.size() + tx.size() ) 
        				pad = (p < 1) ? 0 : p / 2;
        			} // end of if
        		} // end of if

        		if ( pad < 1 )
        		{
        			whole = tx
        		}
        		else 
        		{	
        			whole = ""
        			pad.times{ whole += ' '; }
        			whole += tx
        		} // end of else

        	    // if this is the first time thru for this column, erase the leading c/r
        		( itemflag[thiscolumn] )  ?  itemflag[thiscolumn] = false  :  columns[thiscolumn].appendColumnText('\n')
                columns[thiscolumn].appendColumnText(whole+val)    // stuff in column 1

        } // end of walking thru each title 



	    // find how many blank lines go in final column
        int j = ( ( thiscolumn + 1 ) * percolumn )            

       // add extra blank m/i entries to make last column look nicer on odd m/i count
       j.times{ columns[numberofcolumns-1].appendColumnText("\n") }
    
    } // end of load method



    // ============================================
    // declare text attributes and mono font usage
    public void setColumn(HeaderSupport su)
    {
        def column1 = su.getColumn()
        column1.setBackground(Color.black);
        column1.setFocusable(false)
        column1.setEditable(false);
        column1.setFont(new Font("Monospaced", Font.BOLD, 10));
        column1.setMinimumSize(new Dimension(165,50));
        column1.setPreferredSize(new Dimension(180,60));
        column1.setMaximumSize(new Dimension(500,120));
    } // end of setting attributes 


    // one-time setup of a column text pane and styles with colors to be used as the menu
    public JTextPane getColumnTextPane(StyledDocument document)
    {
        JTextPane jtp = new JTextPane(document);
        jtp.setBorder(null)
        return jtp;
    } // end of getText


    // This is logic to populate the joblog of the menu panel =========================
    // Clear out current document
    private void clearColumnText(HeaderSupport su) 
    {
        su.getColumn().setStyledDocument (doc = new DefaultStyledDocument());
    } // end of clearColumnText

    // method to add text to column pane with specific display attributes
    private void appendColumnText(String s) 
    {
        doc = this.getColumn().getDocument();
        try 
        {
            doc.insertString(doc.getLength(), s, as1);
        }
        catch (BadLocationException e) {}
    } // end of appendColumnText

} // end of HeaderSupport.class