#!/usr/bin/env groovy

def p1 = "${0}"
println("Hello world from ${p1}")
for (a in this.args) {
  println("Argument: " + a)
}
println "the script name is ${p1}"

// Create a ref for closure
def delClos

// Define closure
delClos = { println "Dir ${it.canonicalPath}";
            it.eachDir( delClos );
            it.eachFileMatch(~/.*?\.jpg/) 
            {
                String fn = it.canonicalPath;
                def fi = new File(fn);
                if (fi.exists())
                {
                    print "File ${fn}";
                    def d = JPG.getImageDimension(fn);
                    if (d)  print " has width=${d.width.toInteger()}px height=${d.height.toInteger()}px";
                    println ";"
                }
                //it.delete()
            }
    }

// Apply closure
if (args.length>0) delClos( new File(args[0]) )