#!/usr/bin/env groovy

// Create a ref for closure
def delClos
def menus =[:]

// Define closure
delClos = { 
	    //println "Dir ${it.canonicalPath}";
	    def path = it.canonicalPath
            it.eachDir( delClos );
            it.eachFileMatch(~/.*?\.txt/) 
            {
                String fn = it.canonicalPath;
                def fi = new File(fn);
                if (fi.exists())
                {
			if (fi.getName().startsWith("temp")) 
			{
				fi.delete()
			}
			else
			{
	                    //println "File ${fn}  ------------";
			    lines = fi.readLines()
			    outer: for (line in lines) 
		    	    {	
			 	if (!line.trim()) continue
    				words = line.split(/\:=/).toList()

        			if (words.size() > 1 && words[1].toLowerCase().equals("*menutitle")) 
				{ 
					menus[fn] = words[0]
					break outer;
				} // end of if
		    	    } // end of for
			} // end of else
                } // end of if
            }  // end of eachFile

	def tmp = new File(path+"/.menulist.txt")
	tmp.write("Available Menus:=*MENUTITLE\n");
	int count = 0
	menus.each{ tmp.append ("${it.value} :=go ${it.key}\n"); count+=1; }
	println "$count menus exist. Press F15 to review."
} // end of closure

// Apply closure
if (args.length>0) delClos( new File(args[0]) )