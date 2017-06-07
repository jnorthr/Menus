// #! /bin/bash
// export CLASSPATH=.:/Volumes/Data/dev/groovy/groovy-2.0.0/embeddable/groovy-all-2.0.0-beta-1.jar:$CLASSPATH
// cd /Volumes/Data/dev/groovy/Menus
// java  Splasher 

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.io.File;
import javax.swing.SwingWorker;
import groovy.lang.GroovyShell; // need groovy-all-x.x.x.jar in your CLASPATH env.variable to compile this java source

public class Splasher 
{
    /**
     * Shows the splash screen, launches the application and then disposes
     * the splash screen.
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws Throwable {
    	System.out.println("... starting");
    	String fn = "Menus.groovy";
    	if (args.length>0) fn=args[0];
        SplashWindow.splash(Splasher.class.getResource("./resources/loading.gif"));
	String[] path = {"/Volumes/Data/dev/groovy/Menus"};		
	GroovyShell shell = new GroovyShell();
	shell.run(new File(fn), path);
  
/*	implement worker threads later
   	SwingWorker worker = new SwingWorker()    
   	{  public Object construct() 
   	   {   		   	 
 	   }; // end of construct
   	};
   	worker.start();
*/


//    	shell.run(new File(fn), path);

        //SplashWindow.invokeMain("MyApplication", args);
    	//System.out.println("   dispose");
    	System.out.println("    dispose");
        SplashWindow.disposeSplash();
    	System.out.println("... Splasher ending");
	//System.exit(0);
    }
    
}
