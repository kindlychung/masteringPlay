package models

import play.api.Logger
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Concurrent, Enumeratee, Enumerator, Iteratee}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object StreamData {

  val line: String = "What we need is not the will to believe, but the wish to find out."
  val words: Seq[String] = line.split(" ")

  val src: Enumerator[String] = Enumerator(words: _*)

  val sink: Iteratee[String, Int] = Iteratee.fold[String, Int](0)((x, y) => x + y.length)
  val flow: Future[Iteratee[String, Int]] = src(sink)

  val result: Future[Int] = flow.flatMap(_.run)

  src |>>> sink

  //this can only be used when the application is running
  val dataStream: Enumerator[String] = Enumerator.generateM {
    Promise.timeout(Some("alive"), 100 millis)
  }

  val unicastSrc = Concurrent.unicast[String](
    channel => channel.push(line)
  )

  val unicastResult: Future[Int] = unicastSrc |>>> sink

  val unicastSrc2 = Concurrent.unicast[String](
    channel => channel.push(line),
    onError = { (msg, str) => Logger.error(s"encountered $msg for $str") }
  )

  val (broadcastSrc: Enumerator[String], channel: Concurrent.Channel[String]) = Concurrent.broadcast[String]

  val (en: Enumerator[String], br: Concurrent.Broadcaster) = Concurrent.broadcast[String](src)


  private val vowels: Seq[Char] = Seq('a', 'e', 'i', 'o', 'u')

  def getVowels(str: String): String = {
    val result = str.filter(c => vowels.contains(c))
    result
  }

  def getConsonants(str: String): String = {
    val result = str.filterNot(c => vowels.contains(c))
    result
  }

  val vowelCount: Iteratee[String, Int] = Iteratee.fold[String, Int](0)((x, y) => x + getVowels(y).length)

  val consonantCount: Iteratee[String, Int] = Iteratee.fold[String, Int](0)((x, y) => x + getConsonants(y).length)

  val vowelInfo: Future[Int] = broadcastSrc |>>> vowelCount
  val consonantInfo: Future[Int] = broadcastSrc |>>> consonantCount

  words.foreach(w => channel.push(w))
  channel.end()

  vowelInfo onSuccess { case count => println(s"vowels:$count") }
  consonantInfo onSuccess { case count => println(s"consonants:$count") }

  val toSmallCase: Enumeratee[String, String] = Enumeratee.map[String] {
    s => s.toLowerCase
  }

  src.through(toSmallCase) |>>> vowelCount

  src &> toSmallCase |>>> vowelCount

  src |>>> toSmallCase.transform(vowelCount)

  src |>>> toSmallCase &>> vowelCount

  val filterVowel: Enumeratee[String, String] = Enumeratee.map[String] {
    str => str.filter(c => vowels.contains(c))
  }

  src |>>> toSmallCase.compose(filterVowel) &> vowelCount

  src |>>> toSmallCase ><> filterVowel &> vowelCount

  val toInt: Enumeratee[String, Int] = Enumeratee.map[String] {
    str => str.length
  }

  val sum: Iteratee[Int, Int] = Iteratee.fold[Int, Int](0)((x, y) => x + y)

  src |>>> toSmallCase ><> filterVowel ><> toInt &>> sum

}
