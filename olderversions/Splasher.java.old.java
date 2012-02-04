// export CLASSPATH=.:/Developer/Development/personal/groovy/groovy-1.6.4/embeddable/groovy-all-1.6.4.jar:$CLASSPATH
// javac Splasher.java
// java  Splasher HelloWorld.groovy
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.io.File;
import groovy.lang.GroovyShell;

public class Splasher 
{
    /**
     * Shows the splash screen, launches the application and then disposes
     * the splash screen.
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws Throwable {
    	System.out.println("... starting");
    	String fn = "menus.groovy";
    	if (args.length>0) fn=args[0];
        SplashWindow.splash(Splasher.class.getResource("loading.gif"));
    	System.out.println("    invoke groovy script "+fn);

    	String[] path = {"/Volumes/UBUNTU/Groovy/Menus"};		
    	GroovyShell shell = new GroovyShell();
    	shell.run(new File(fn), path);

        //SplashWindow.invokeMain("MyApplication", args);
    	//System.out.println("   dispose");
    	System.out.println("    dispose");
        SplashWindow.disposeSplash();
    	System.out.println("... Splasher ending");
    }
    
}
