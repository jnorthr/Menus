import java.io.*;
import java.util.*;
public class ShowProp
{
  public static void main(String[] args)
  {
    // Get all system properties
    Properties props = System.getProperties();
    
    // Enumerate all system properties
    Enumeration en = props.propertyNames();
    for (; en.hasMoreElements(); ) {
        // Get property name
        String propName = (String)en.nextElement();
    
        // Get property value
        String propValue = (String)props.get(propName);
	System.out.println(propName + " " + propValue);
    };

  String propValue = (String)props.get("os.name");
  System.out.println("os.name=" + propValue);

  };  //end of main
}; // end of class