package com.github.kreig133.dumpanalyze.parsing

import java.time.LocalDateTime

object ThreadState extends Enumeration {
  type State = Value
  val RUNNABLE, WAITING, BLOCKING, SLEEPING = Value
}

case class Thread(vid: String, nid: String, daemon: Boolean, state: ThreadState.Value, trace: List[String])

case class ThreadDump(time: LocalDateTime, threads: List[Thread]) {
  override def toString: String = s"ThreadDump($time threads = \n${threads.mkString("\n")})"
}

object Parser {
  private[this] val ThreadLine = "^\"([^\"]+)\"(?: #(\\d+))?( daemon)?(?: prio=(\\d+))?(?: os_prio=(\\d+))? tid=(\\S+) nid=(\\S+) ([^\\[\\n]+)(?: \\[([^]]+)\\])?$".r
  private[this] val Date   = "(\\d{4})-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)".r
  private[this] val Stacktrace = "\tat (.*)".r
  private[this] val State = "   java.lang.Thread.State: (\\w+).*".r


  def main(args: Array[String]) {
    import scala.io.Source
    parse(Source.fromFile(args(0)).getLines())
  }



  def parse(lines: Iterator[String]) : ThreadDump = {
    def parseInner(date: Option[LocalDateTime], currentThread: Option[Thread], stacktrace: List[String], threads: List[Thread], restLines: List[String]) : ThreadDump = {
      restLines match {
        case Nil => ThreadDump(date.getOrElse(LocalDateTime.now()),
                                  currentThread match {
                                    case None   => threads
                                    case Some(t) => t.copy(trace = stacktrace) :: threads
                                  })
        case curr :: rest =>
          curr match {
            case ThreadLine(name, number, daemon, prio, os_prio, tid, nid, status, monitor) =>
              parseInner(
                date,
                Some(Thread(name, nid, daemon != null, ThreadState.RUNNABLE, Nil)),
                Nil,
                currentThread match {
                  case None =>     threads
                  case Some(t) =>  t.copy(trace = stacktrace) :: threads
                }, rest)

            case State(state) => parseInner(date, Some(currentThread.get.copy(state = ThreadState.withName(state))), stacktrace, threads, rest)
            case Stacktrace(trace) => parseInner(date, currentThread, trace::stacktrace, threads, rest)
            case Date(year, month, day, hour, minute, second) =>
              parseInner(Some(LocalDateTime.of(year.toInt, month.toInt, day.toInt, hour.toInt, minute.toInt, second.toInt)),
               currentThread, stacktrace, threads, rest)
            case other =>
              println("other = " + other)
              parseInner(date, currentThread, stacktrace, threads, rest)
          }
      }
    }

    parseInner(None, None, Nil, Nil, lines.toList)
  }
}
