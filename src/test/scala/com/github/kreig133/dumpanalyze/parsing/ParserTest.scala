package com.github.kreig133.dumpanalyze.parsing

import java.net.URL

import org.junit.Test

import scala.io.Source

class ParserTest {


  @Test
  def test(): Unit = {

    val resource: URL = this.getClass.getClassLoader.getResource("1.tdump")
    println(Parser.parse(Source.fromFile(resource.toURI).getLines()))
  }

  val str1 = "\"main\" #1 prio=5 os_prio=31 tid=0x00007fce0b006000 nid=0xe07 runnable [0x0000000104c67000]"

  val str2 = "\"GC task thread#1 (ParallelGC)\" os_prio=31 tid=0x00007fce0a809800 nid=0x2313 runnable "


  val str3 = "\"Finalizer\" #3 daemon prio=8 os_prio=31 tid=0x00007fce0b84c800 nid=0x2d03 in Object.wait() [0x0000000119a1a000]"

  @Test
  def testRegex() : Unit = {
    parse(str1)
    parse(str2)
    parse(str3)


  }

  val regex = "^\"([^\"]+)\"(?: #(\\d+))?( daemon)?(?: prio=(\\d+))?(?: os_prio=(\\d+))? tid=(\\S+) nid=(\\S+) ([^\\[\\n]+)(?: \\[([^]]+)\\])?$".r


  def parse(str: String): Unit = {

    println("str = " + str)
    str match {
      case regex(name, number, daemon, prio, os_prio, tid, nid, status, monitor) =>
        println("name = " + name)
        println("number = " + number)
        println("daemon = " + daemon)
        println("prio = " + prio)
        println("os_prio = " + os_prio)
        println("tid = " + tid)
        println("nid = " + nid)
        println("status = " + status)
        println("monitor = " + monitor)
    }
  }


}
