// sample java process examples can be found here: http://www.rgagnon.com/javadetails/java-0014.html



//Here is an example that starts a process with a modified working directory and environment, and redirects standard output and error to be appended to a log file:
// http://java.sun.com/javase/7/docs/api/java/lang/ProcessBuilder.html
 ProcessBuilder pb = new ProcessBuilder("myCommand", "myArg1", "myArg2");
 Map<String, String> env = pb.environment();	// get current environment
 env.put("VAR1", "myValue");
 env.remove("OTHERVAR");
 env.put("VAR2", env.get("VAR1") + "suffix");
 def cwd =  pb.directory()		// returns current working dir
 pb.directory(new File("myDir"));	// sets new current working dir
 File log = new File("log");		// make file for output
 pb.redirectErrorStream(true);
 pb.redirectOutput(Redirect.appendTo(log));
 Process p = pb.start();	// make it run
 assert pb.redirectInput() == Redirect.PIPE;
 assert pb.redirectOutput().file() == log;
 assert p.getInputStream().read() == -1;
 
//To start a process with an explicit set of environment variables, first call Map.clear() before adding environment variable
// ---------------------------------------
// java 1.5 or later
import java.io.*;
import java.util.*;

public class CmdProcessBuilder {
  public static void main(String[] args) 
     throws InterruptedException,IOException 
  {
    List<String> command = new ArrayList<String>();
    command.add(System.getenv("windir") +"\\system32\\"+"tree.com");
    command.add("/A");

    ProcessBuilder builder = new ProcessBuilder(command);
    Map<String, String> environ = builder.environment();
    builder.directory(new File(System.getenv("temp")));

    System.out.println("Directory : " + System.getenv("temp") );
    final Process process = builder.start();
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    while ((line = br.readLine()) != null) {
      System.out.println(line);
    }
    System.out.println("Program terminated!");
  }
}