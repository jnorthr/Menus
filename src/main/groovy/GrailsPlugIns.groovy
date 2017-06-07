def addr = "http://plugins.grails.org/.plugin-meta/plugins-list.xml"
def plugins = new XmlSlurper().parse(addr)
def count = 0
plugins.plugin.each{
  println it.@name
  count++
}
println "Total number of plugins: ${count}"