package com.jnorthr.tools;

// export CLASSPATH=.:/Volumes/Data/dev/groovy/groovy-2.0.0/embeddable/groovy-all-2.0.0.jar:$CLASSPATH
// RedApple with groovy 1.8 installed:
// export CLASSPATH=.:/Volumes/Data/dev/groovy/groovy-2.0.0/lib/groovy-2.0.0.jar:$CLASSPATH
// javac Splasher.java
// groovy -cp . Splasher

// could test like this :
// java  Splasher HelloWorld.groovy
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.io.File;
//import groovy.lang.GroovyShell;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Splasher 
{
    /**
     * Shows the splash screen, launches the application and then disposes
     * of the splash screen.
     * @param args the command line arguments
     */
    public static void main(String[] args)  throws Throwable {
    	System.out.println("... Splasher starting");
    	String fn = "menus.groovy";
    	if (args.length>0) fn=args[0];
        SplashWindow.splash(Splasher.class.getResource("./resources/loading.gif"));
    	System.out.println("    invoke groovy script "+fn);

    	String[] path = {"/Volumes/Data/dev/groovy/Menus"}; //{"/Volumes/UBUNTU/Groovy/Menus"};		

    	//GroovyShell shell = new GroovyShell();
        //ScriptEngineManager factory = new ScriptEngineManager();
        //ScriptEngine shell = factory.getEngineByName("groovy");
    	//shell.run(new File(fn), path);
	//shell.eval("/Users/jim/Desktop/Menus/menus.groovy");

        SplashWindow.invokeMain("Menus", args);
    	System.out.println("    dispose");
        SplashWindow.disposeSplash();
    	System.out.println("... Splasher ending");
    }
    
}
