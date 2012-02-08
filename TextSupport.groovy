// allow hash char. #  using alt-3 on keyboard
import java.awt.Color;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.*
import groovy.swing.SwingBuilder

public class TextSupport
{
    def jtp;
    StyledDocument doc;
    def at; // pointer to semi-colon in text stream
    def foundsemi;    // boolean flag true if location of semi-colon within allowable range
    def colorsignature = 0;
    def printed = false;
    def audit = false;
    def as0 = new SimpleAttributeSet();
    
    // simple constructor
    public TextSupport()
    {
        jtp = new JTextPane();   
        Font font = new Font("Dialog", Font.BOLD, 12);
        jtp.setFont(font);
        jtp.setForeground(Color.white);             
        jtp.setBackground(Color.black);             
    } // end of constructor
    

    // ==========================================
    // print audit trail ========================
    def say(txt)
    {
        //if (audit) println "TS : $txt";
    }


    // ================================================================
    // method to add text to pane with specific display attributes; copied from Support.groovy
    private void appendText(String s, AttributeSet attributes) 
    {
        String sb = '\n'+s
        doc = jtp.getDocument();
        jtp.grabFocus();
        try 
        {
            doc.insertString(doc.getLength(), sb, attributes);
        }
        catch (BadLocationException e) {}
    } // end of appendText

    
    // ================================================================        
    // method to add text to pane with no display attributes; copied from Support.groovy
    private void appendText(String s) 
    {
        as0 = new SimpleAttributeSet();
        def col = Color.decode("#ffffff")
        StyleConstants.setForeground(as0, col); 
        appendText(s, as0);
    } // end of appendText
     
            
    // --------------------------------------------------------------
    // produces full result of writing text in a hex color to the jtextpane document; 
    private writeText(text)
    {
        appendText(text);
     } // end of method


    // ===============================================================================
    // produces full result of writing text in a hex color to the jtextpane document
    // color is integer version of something like 0xff00ff; style of declaration
    private writeText(text, String number)
    {
        as0 = new SimpleAttributeSet();
        String num = "#";

        if ( number==null || number.size() < 1 || number.equals("-1") ) 
        {
           num+="000000"; 
        }
        else      
        {
           num+=number 
        }

        try
        {            
            // create a Color object
            def col = Color.decode(num)
            //Color col = Color.decode("FF0096")
            StyleConstants.setForeground(as0, col); 
        }
        catch (Exception x)
        {
            //x.printStackTrace();
            println "... : could not convert color of <$number>"
        } // end of catch
                
        appendText(text, as0);        

    } // end of method


// --------------------------------------------------------------
// test my new class
public static void main(String[] args)
{
    println "\n===================== Start ======================================="
    // My new class
    TextSupport ts = new TextSupport();
    def test = "#c00088;This is a test of one text parm prefixed with color #c00088;This..."
    ColorManager cm = new ColorManager(test);
    String c = cm.getHexCode();
    def t = cm.getText();
    ts.writeText(t, c);
    
    test = "red;Display Max Menu";
    cm = new ColorManager(test);
    c = cm.getHexCode();
    t = cm.getText();
    ts.writeText(t, c);
 

    test = "This is a test of one text parm without color prefix"
    ts.writeText("the second test of getColor($test) returned <$c>");

    test = "0xccfe;This is a test of one text parm color prefix";
    cm = new ColorManager(test);
    c = cm.getHexCode();
    t = cm.getText();
    ts.writeText(t, c);


    test = "brown;Display 0xbrown; colored text";
    cm = new ColorManager(test);
    c = cm.getHexCode();
    t = cm.getText();
    ts.writeText(t, c);

    ts.writeText("the fourth test of getColor($test) returned <$c> and text of <$t>\n");


    test = "brown;Display #brown; colored text";
    cm = new ColorManager(test);
    c = cm.getHexCode();
    t = cm.getText();
    ts.writeText(t, c);

    test = "the fifth test of getColor($test) using # as color sig. returned <$c> and text of <$t>";
    cm = new ColorManager(test);
    c = cm.getHexCode();
    t = cm.getText();
    ts.writeText(t, c);  

    
    ts.writeText("Start of leanne.txt ----------------------------")
    
    def f = new File("../menudata/leanne.txt")   // get handle for the menu text file
        f.eachLine         // walk thru each line of menu file ignoring comment lines starting with //
        {    aline -> 
                    cm = new ColorManager(aline);
                    c = cm.getHexCode();
                    t = cm.getText();
                    ts.writeText(t, c);
                //ts.appendText(lines);
        }
    // end of file input


    def swing = new SwingBuilder()
    def frame = swing.frame(title:"test", background:Color.black, pack:false, location:[20,20], size:[600,700], show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE) 
        {   
            sp1 = scrollPane(id:'sp1',border:null,minimumSize:[590,690]) {widget( ts.jtp ) }
            
        }

    println "=====================  End  =======================================\n"

    } // end of main
    
} // end of class