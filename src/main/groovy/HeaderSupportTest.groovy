// test class to build text panes of menu items to mimic multiple columns; 
import javax.swing.*
import java.awt.*
import javax.swing.text.*;


import static java.awt.GridBagConstraints.*
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import groovy.swing.SwingBuilder

public class HeaderSupportTest
{
	// print debug text (maybe)
	public static void say(def text) 
	{
		if (true) {println "$text";} 
	} // end of say


	// test harness for this class
	public static void main(String[] args)
	{	
		say "... started"
		def frametitle = "Header Example"
		def columns = []
		def col
		//3.times{col = new HeaderSupport(); columns << col;}
		3.times{columns << new HeaderSupport(); }
		
		def mi = ["System   :":"os.name", "Hostname : ":"hostname", "Network  :":"inet", "Mouseless Menus ":"menu.version", "  user.home : ":"user.home", "  user.dir :":"user.dir", "Date :":"date", "Time :":"time", "User :":"user", "User Id ":"uid"]
		HeaderSupport.loadMenu(columns,mi)


		def c = new GridBagConstraints();	
		c.insets = new Insets(0, 1, 0, 1);
		c.weighty = 0.1;
		c.weightx = 0.0;
		c.gridx = 0; // column number where zero is first column
		c.gridy = 0; // row number where zero is first row
		c.anchor = GridBagConstraints.NORTH; // direction of drift 4 this component when smaller than window 
		c.fill = GridBagConstraints.HORIZONTAL; // rule to let a component expand both ways when more space is available on resize
		c.ipadx = 1
		c.ipady = 0
 
		JFrame.setDefaultLookAndFeelDecorated(true);
		def swing = new SwingBuilder()
		def frame = swing.frame(title:"${frametitle}", background:Color.black, size:[800,300], show:true, defaultCloseOperation:JFrame.EXIT_ON_CLOSE, layout:new GridLayout(3,1)) 
		{
		   panel(layout:new GridBagLayout(), background:Color.BLACK)
		   {
			c.weighty = 0.0;
			c.weightx = 1.0;
			//c.gridy += 0.75;  				
			c.fill = GridBagConstraints.BOTH; // HORIZONTAL
			columns.each 
			{
				widget(it.getColumn(),constraints: c)
				c.gridx += 1;  				
			} // end of each
		   } // end of panel

		   panel(background:Color.RED)
		   {
			label "hi kids"
		   } // end of panel
		} // end of frame

		say "... ended"
	} // end of main

} // end of HeaderSupportTest.class
