// utility to receive a list of absolute filenames from a given path and having a given filename suffix
// For example: groovy MenuList.groovy /Volumes/Data/dev/Menus/data .prop*
//     to get list of any filename from '/Volumes/Data/dev/Menus/data' where filename ends with .prop*
// and example: groovy MenuList.groovy /Volumes/Data/dev/Menus/data
//     to get list of any filename from '/Volumes/Data/dev/Menus/data' where filename ends with .txt (the default if second parm is missing)

/*  this might also work :
We can retrieve a list of files from a directory:
 
assert new File('D:/Groovy/Scripts').list().toList() ==
  ['Script.bat', 'File1.txt', 'File2.txt', 'Directory1', 'Directory2']
    //list() returns an array of strings

*/

import groovy.util.AntBuilder 

class MenuLists 
{ 
  def menufile 

  // constructor
  MenuLists(String menuname)
  {   
    menufile = menuname   
  } 

  // see sample assert above - looks much more simple
  static anotherWay(dir)
  {
 	def menulist = new File(dir).list().toList() 
	menulist.each{ if (it.toLowerCase().endsWith(".txt")) 
		println it; 
	}
  } // end of method

  // for toString() below
  String getTitle()
  { 
    menufile
  } 

  String toString()
  { 
    return  "Menu file: ${getTitle()}" 
  } 

  // ant file scanner looks thru files in 'sdir' for any files having a suffix like this 
  static List getMenuListsForDirectory(String sdir, String suffix)
  { 
    println "sdir is: ${sdir}" 
    def ant = new AntBuilder() 
    def scanner = ant.fileScanner 
    { 
       fileset(dir:sdir) 
       { 
	 def path = "**/*${suffix}"  	// groovy MenuList.groovy ./data  .prop*    
         include(name:path) 		// would look for ./data/*.prop* files
       } 
    } 

    def menus = [] 
    scanner.each{ f ->   
      menus << new MenuLists(f.getAbsolutePath())   
    } // end of scanner

    return menus 
  } // end of getMenuListsForDirectory method


  // call ant file scanner to look in 'dir' folder for any '*.txt' files; for example: groovy MenuList.groovy ./data  
  static List getMenus(String dir)
  {
	def m = getMenus(dir,".txt")
	return m
  }
 
  // call ant file scanner to look in 'dir' folder for any 'suffix' files; for example: groovy MenuList.groovy ./data  .prop*
  static List getMenus(String dir, String suffix)
  {
	def menus = MenuLists.getMenuListsForDirectory(dir,suffix) 
	menus.each{ println it } // end of each
	return menus
  } // end of getMenus

  // main method
  public static void main(String[] args)
  {
	if (args.length<1)
	{
		println "help !"
		println "need command line parameter naming folder with menu files"
		println "like: groovy MenuList.groovy ./data"
		System.exit(0);
	}

	def suffix = (args.length<2) ? ".txt" : args[1];
	def m = (args.length<2) ? MenuLists.getMenus(args[0]) : MenuLists.getMenus(args[0],suffix);
	
	println "found ${m.size()} menus" 
	m.each{ println it } // end of each

	println "---------- another way -----------------"
	MenuLists.anotherWay(args[0]);
	println "… end"
  }
}  // end of class 

