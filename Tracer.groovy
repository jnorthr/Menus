public class Tracer
{
	// ====================================================
	// method to print failure stack trace in a nice formt
	public displayStackTraceInformation (Throwable ex, boolean displayAll)
    {
    	def sb ="";
    	if (null == ex)
    	{
    		sb+= "Null stack trace reference! Bailing...";
        return sb;
    	}

    	sb+= "The stack according to printStackTrace():\n";

    	StackTraceElement[] stackElements = ex.getStackTrace();
    	if (displayAll)
    	{
        		sb += "The " + stackElements.length +
                                " element" +
                                ((stackElements.length == 1) ? "": "s") +
                                " of the stack trace:\n";
            return sb;
       }
       else
       {
           	sb += "The top element of a " +
                            stackElements.length +
                            " element stack trace:\n";
            return sb;
       }
    
       for (int lcv = 0; lcv < stackElements.length; lcv++)
       {
       	  sb += "Filename: "+stackElements[lcv].getFileName();
          sb += "Line number: "+ stackElements[lcv].getLineNumber();
          String className = stackElements[lcv].getClassName();
          String packageName = extractPackageName (className);
          String simpleClassName = extractSimpleClassName (className);

          sb += "Package name: " +  ("".equals (packageName)) ?  "[default package]" : packageName;
          sb += "Full class name: " + className;
          sb += "Simple class name: " + simpleClassName;
          sb += "Direct class name: " +  extractDirectClassName (simpleClassName);
          sb += "Method name: " + stackElements[lcv].getMethodName();
          sb += "Native method?: " +   stackElements[lcv].isNativeMethod();
          sb += "toString(): " +  stackElements[lcv].toString();
      
          if (!displayAll)
          	 return sb;
   		}	// end of for
   		
   		say ("");

   		return sb;
    }       // End of displayStackTraceInformation().


    // -----------------------------------------------------------
    // discover PackageName
    public static String extractPackageName (String fullClassName)
    {
    	if ((null == fullClassName) || ("".equals (fullClassName)))
    		return "";

    	int lastDot = fullClassName.lastIndexOf ('.');
    	if (0 >= lastDot)
    		return "";
    
    	return fullClassName.substring (0, lastDot);
    } // end of extractPackageName


    // discover ClassName
    public static String extractSimpleClassName (String fullClassName)
    {
    	if ((null == fullClassName) || ("".equals (fullClassName)))
    		return "";

    	int lastDot = fullClassName.lastIndexOf ('.');
    	if (0 > lastDot)
    		return fullClassName;
    
    	return fullClassName.substring (++lastDot);
    } // end of extractSimpleClassName


    // discover ClassName
    public static String extractDirectClassName (String simpleClassName)
    {
    	if ((null == simpleClassName) || ("".equals (simpleClassName)))
    		return "";

    	int lastSign = simpleClassName.lastIndexOf ('$');
    	if (0 > lastSign)
    		return simpleClassName;

    	return simpleClassName.substring (++lastSign);
    } // end of extractSimpleClassName

} // end of class
