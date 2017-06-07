import java.awt.EventQueue;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class URLClass {
  JFrame frame;
  static String address = "http://www.java2s.com";

  public void URLSubClass()
  {
    frame = new JFrame("URL Display Panel");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    try {
      JEditorPane editorPane = new JEditorPane(address);
      editorPane.setEditable(false);

      HyperlinkListener hyperlinkListener = new ActivatedHyperlinkListener(
          editorPane);
      editorPane.addHyperlinkListener(hyperlinkListener);

      JScrollPane scrollPane = new JScrollPane(editorPane);
      frame.add(scrollPane);
    } catch (IOException e) {
      System.err.println("Unable to load: " + e);
    }

    frame.setSize(800, 600);
    frame.setVisible(true);
  } // end of constructor

  static void getPage(String arg)
  {
    address = arg;
    URLClass urlc = new URLClass();
    urlc.URLSubClass();
  } // end of method

  public static void main(final String args[]) 
  {
    if (args.length >0) address = args[0];


    URLClass urlc = new URLClass();
    if (args.length >0) 
    {
    	urlc.getPage(args[0]);
    }
    else
    {
    	    urlc.URLSubClass();
    } // end of else
  } // end of main

} // end of class

class ActivatedHyperlinkListener implements HyperlinkListener {

  JEditorPane editorPane;

  public ActivatedHyperlinkListener(JEditorPane editorPane) {
    this.editorPane = editorPane;
  }

  public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
    HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
    final URL url = hyperlinkEvent.getURL();
    if (type == HyperlinkEvent.EventType.ENTERED) {
      //System.out.println("URL: " + url);
    } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
      //System.out.println("Activated");
      Document doc = editorPane.getDocument();
      try {
        editorPane.setPage(url);
      } catch (IOException ioException) {
        System.out.println("Error following link");
        editorPane.setDocument(doc);
      }
    }
  }
}

