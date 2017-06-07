/*       an earlier version of the logic to build a system header set as a maximum width of 132 chars; see HeaderSupport for a later way to do this worked ok but columns would not move when window was resized.
        user.home /Users/leanne
        user.dir /Volumes/UBUNTU/Groovy/Menus
        x java.runtime.version 1.6.0_15-b03-226
        x os.name=Mac OS X os.version 10.5.8
        http.proxyHost 10.0.1.4 https.proxyPort 8080
---------  */

public class CommandSet
{
    def cmd = "command set"	// mac os command to see environmental variables
    def txt
    def map =[:]
    String propertyfile = '../menudata/menu.properties'  		// non-OS specific parameters for business issues
    def config

    public CommandSet()
    {
       	config = new ConfigSlurper().parse(new File(propertyfile).toURL())	// get non-path related static values

        Properties props = System.getProperties()

    	def tx

    	config.propertykeys.each 
    	{ k ->  
    		tx = (props.get(k)) ?  (String)props.get(k) : "unknown"  
            map.put(k,tx)
    	} // end of each
            setDate()

    	map.put("menu.version","V1.0");
       //def words=[]
        def word1

    	// logic to ask mac os to provide list of known environmental properties - may not work on ubuntu or windows
        txt = cmd.execute().text
        txt.eachLine
        {
            line -> 
        		if (line.trim().size() > 0)
        		{
        	            def words = line.split(/=/)
        		    //println "<"+line+"> has "+words.size()+" words";
        		    if (words.size()>1)
        		    {
                    		    word1=words[0].toLowerCase()
                    		    if (config.systemkeys.contains(word1)) {map.put(word1,words[1])}   
        		    }
        		} // end of if
        } // end of eachLine

        txt = "ifconfig".execute().text
        txt.eachLine
        {
            line -> 
            	if (line.trim().size() > 0)
            	{
            		def tokens = line.trim().split()
            		//println tokens[0]+" "+tokens[1]
            		if (tokens[0].toLowerCase()=="inet") {map.put(tokens[0],tokens[1])}
            	} // end of if
    	} // end of eachLine

    } // end of constructor


    // get handle to config
    def getConfig()
    {
        return config
    } // end of get

    
    // get property map
    def getMap()
    {
        return map
    } // end of get


    // fill map with date and time
    def setDate()
    {
        def words = new Date().toString().split(' ')
        def dat = words[0]+' '+words[2]+' '+words[1]+' '+words[5]		// like  Mon 21 Dec 2009
        def tim = words[3]+' '+words[4]        					// like 16:37:40 CET
        map.put("date",dat)
        map.put("time",tim)
    } // end of setDate
    

    // get a map entry, if found, then return map.value else return 'not declared'
    def getMap(k)
    {
    	return (this.map[k]==null) ? "not declared" : this.map[k] 
    }


    // ============================
    // test harness for this class
    public static void main(String[] args)
    {    
        println "---> starting"
        def parm = "/Volumes/USBKEY/Menus/"
        args.each{a -> parm=a    } // end of each

        CommandSet cs = new CommandSet()
    	def key = "path"
        def q = cs.getMap()[key]	// this method gets a map reference then uses a [key] to find a value in that map
        println "$key="+q				// but getMap(k) is prettier as null keys return 'not declared'

    	key = "date"
    	println "$key="+cs.getMap(key)

        println "... end"
    } // end of main

} // end of class
