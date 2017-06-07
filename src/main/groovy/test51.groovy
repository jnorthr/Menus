// try to get output from a program
println "==============\nTest 1"
def sout = new StringBuffer(), serr = new StringBuffer()
def proc = 'ls /badnews'.execute()
proc.consumeProcessOutput(sout, serr)
proc.waitForOrKill(1000)
println "out> $sout err> $serr"

println "==============\nTest 2"
def proc2 = "echo hikids".execute()
def b2 = new StringBuffer()
proc2.consumeProcessErrorStream(b2)

println proc2.text
println b2.toString()

println "==============\nTest 3"
//def proc3 = "\$GROOVY_HOME/bin/groovy -v".execute()
def proc3 = "groovy -v".execute()
def b3 = new StringBuffer()
proc3.consumeProcessErrorStream(b3)

println proc3.text
println b3.toString()

println "===========================================\nTest 4"
def command4 = """javac -version"""// Create the String
def proc4 = command4.execute()                 // Call *execute* on the string
proc4.waitFor()                               // Wait for the command to finish

// Obtain status and output
println "return code: ${ proc4.exitValue()}"
println "stderr: ${proc4.err.text}"
println "stdout: ${proc4.in.text}" // *out* from the external program is *in* for groovy

println "==============\nTest 5"
//def proc3 = "\$GROOVY_HOME/bin/groovy -v".execute()
def proc5 = "groovy -v".execute()
def b5 = new StringBuffer()
proc5.consumeProcessErrorStream(b5)

println proc5.text
println b5.toString()



println("===============\nTest 6 ==========\nGetting an RSS yahoo weather feed is as simple as making a plain old HTTP GET request.")
// Parsing an RSS Feed
def rssFeed2 = "http://weather.yahooapis.com/forecastrss?p=94089".toURL().text
def rss2 = new XmlSlurper().parseText(rssFeed2) // eats the returned xml doc

// show the results of the weather forecast
println("==================================================================")
println rss2.channel.title
println rss2.channel.link
println rss2.channel.description
println rss2.channel.lastBuildDate
println("==================================================================")
rss2.channel.item.each
{
  println it.title
  println it.link
  println it.description
  println it.yweather
  println "-----"
}
println("==================================================================")
