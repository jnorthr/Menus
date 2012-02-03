// module to find a true-type monospaced font filename; uses 1st word of the o/s name as key into a paths property file
// does NOT define the font size or special features like BOLD - just the font filename - then confirms it exists
import groovy.text.Template
import groovy.text.SimpleTemplateEngine
// need this for property retrieval
import java.io.*;
import java.util.*
import java.awt.*
import javax.swing.*
import java.awt.Font

/*
For maximum portability, use the generic font names, but you can use any font installed in the system. It is suggested to use a font family name, and create the font from that, but you can also use the fonts directly. You can get an array of all available font family names or all fonts. 

// Font info is obtained from the current graphics environment.
GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

//--- Get an array of font names (smaller than the number of fonts)
String[] fontNames = ge.getAvailableFontFamilyNames();

//--- Get an array of fonts.  It's preferable to use the names above.
Font[] allFonts = ge.getAllFonts();
*/


class FontSupport
{
	String pathpropertyfile = './data/path.properties'
  
	// OS-specific parms; cannot be in same config file as config.rewrite looses some path info
	def os = System.getProperty('os.name')

	def paths
	def fontpath
	def monofontfilename

	def static audit = false
	public void say(def text) 
	{
		if (audit) {println "$text";} 
	} // end of echo

	public getMonoFontFilename()
	{
		return monofontfilename
	}

	public getFontPath()
	{
		return fontpath
	}

	// ===============
	// constructor
	// ===============
	public FontSupport() 
	{ 
		// Use operating system property first word
		say "os name is $os"
  		def tokens = os.toLowerCase().split(' ').toList()
  		def osid = tokens[0]
		say("... FontSupport() set to use paths for $osid")

		paths = new ConfigSlurper(osid).parse(new File(pathpropertyfile).toURL())
		say("paths is set to use '$osid' paths")

		fontpath = paths.fontpath
		monofontfilename = paths.monofontfilename

		
		say("the fontpath is '$fontpath'")
		say("the monofontfilename is '$monofontfilename'")

		// build full path+filename like c:\windows\font\cour.ttf
		def fn = fontpath+monofontfilename
		monofontfilename = fn
		say("the full font name is '$fn'")

		// confirm font file exists or fail if not
		def fi = new File(fn)
		if(fi.exists()) {say "$fn was found"}
		else
		{
			def todo = "$fn was not found\nChange $pathpropertyfile to point to a valid font"
			int messageType = JOptionPane.INFORMATION_MESSAGE; // no standard icon
			JOptionPane.showMessageDialog(null, "$todo", "Font File Missing", messageType);
		}
		say("... FontSupport() end of constructor ...")

    } // end of default constructor


    // test harness for this class
    public static void main(String[] args)
    {	
		FontSupport ivs = new FontSupport()
		ivs.say "font file name is ${ivs.getMonoFontFilename()}"
    } // end of main
};    // end of class 


