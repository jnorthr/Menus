
/**
* This class creates a frame with a JEditorPane for loading HTML
* help files
*/
import java.io.*;
import javax.swing.event.*;
import javax.swing.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.Desktop;

public class HelpWindow extends JFrame implements ActionListener{
    private final int WIDTH = 640;
    private final int HEIGHT = 400;
    private JEditorPane editorpane;
    private URL helpURL;
/**
* HelpWindow constructor
* @param String and URL
*/
public HelpWindow(String title, String fileurl) 
{
    super(title);
    helpURL = ClassLoader.getSystemResource(fileurl);
    editorpane = new JEditorPane();
    editorpane.setEditable(false);
    editorpane.setContentType("text/html");

    try 
    {   
    	    editorpane.setPage(helpURL);
    } 
    catch (Exception ex) 
    {
        ex.printStackTrace();
    }

    //anonymous inner listener
    editorpane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent ev) {
            try {
                if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String temp = ev.getURL().toURI().toString();
		        System.out.println( "calling URL "+temp );
			String xxx  = ev.getURL().toString();
			URL nextURL = new URL(xxx);
		        System.out.println( "nextURL "+nextURL+" and xxx="+xxx );
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		     	//desktop.getDesktop().browse( nextURL.toURI() );	//editorpane.setPage(ev.getURL());
		        BareBonesBrowserLaunch.openURL(xxx); 
                }
            } catch (Exception ex) {
                //put message in window
                ex.printStackTrace();
            }
        }
    });

    getContentPane().add(new JScrollPane(editorpane));
    addButtons();
    // no need for listener just dispose
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // dynamically set location
    calculateLocation();
    setVisible(true);
    // end constructor
} // end of method

/**
* HelpWindow constructor
* @param String and URL
*/
public HelpWindow(String title, URL helpURL) 
{
    super(title);
    editorpane = new JEditorPane();
    editorpane.setEditable(false);
    try {
        editorpane.setPage(helpURL);
    } catch (Exception ex) {
        ex.printStackTrace();
    };
    getContentPane().add(new JScrollPane(editorpane));
    addButtons();
    // no need for listener just dispose
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // dynamically set location
    calculateLocation();
    setVisible(true);
    // end constructor
} // end of method


public void actionPerformed(ActionEvent e) {
    String strAction = e.getActionCommand();
    URL tempURL;
    try {


        if (strAction == "Close") 
        {
            // more portable if delegated
            processWindowEvent(new WindowEvent(this,
                WindowEvent.WINDOW_CLOSING));
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}


/*
    JButton btncontents = new JButton("Contents");
    btncontents.addActionListener(this);
        if (strAction == "Contents") {
            tempURL = editorpane.getPage();
            editorpane.setPage(helpURL);
        }
*/
private void addButtons() {
    JButton btnclose = new JButton("Close");
    btnclose.addActionListener(this);
    //put into JPanel
    JPanel panebuttons = new JPanel();
    //panebuttons.add(btncontents);
    panebuttons.add(btnclose);
    //add panel south
    getContentPane().add(panebuttons, BorderLayout.SOUTH);
}

private void calculateLocation() {
    Dimension screendim = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(new Dimension(WIDTH, HEIGHT));
    int locationx = (screendim.width - WIDTH) / 2;
    int locationy = (screendim.height - HEIGHT) / 2;
    locationx = 1;
    setLocation(locationx+3, HEIGHT-5);
}
public static void main(String [] args){
	String fn = "./data/help.html";
	if (args.length < 1) {System.out.println("=> missing filename command line argument");}else{fn=args[0];};
        if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }

	new HelpWindow("Test", fn);

}
}//end HelpWindow class
